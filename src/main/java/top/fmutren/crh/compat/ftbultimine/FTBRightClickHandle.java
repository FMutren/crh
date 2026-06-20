package top.fmutren.crh.compat.ftbultimine;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import static top.fmutren.crh.interaction.StateSwitch.iterationTypeForItem;
import static top.fmutren.crh.interaction.TryToEncase.tryToEncaseAllType;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isEncasedCogwheel;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isEncasedShaft;


public class FTBRightClickHandle {
    public static void FTBRightClickEventHandler() {
        RegisterRightClickHandlerEvent.REGISTER.register(registry ->
                registry.registerHandler((context,
                                       hand,
                                       positions) ->
        {
            Player player = context.player();
            if(player.isSpectator() || !player.mayBuild()) return 0;
            Level level = player.level();
            ItemStack heldItem = player.getItemInHand(hand);
            BlockState originState = level.getBlockState(context.origPos());
            Item targetItem = originState.getBlock().asItem();

            int count = 0;

            switch (iterationTypeForItem(heldItem)) {
                case WRENCH -> {
                    if (originState.getBlock() instanceof EncasedPipeBlock)
                        targetItem = AllBlocks.FLUID_PIPE.asItem();
                    for (BlockPos pos : positions) {
                        if (originState.getBlock() instanceof BeltBlock){
                            ftbCompatHandleWrench(level, pos, player, hand, heldItem);
                            return 1;
                        }
                        ftbCompatHandleWrench(level, pos, player, hand, heldItem);
                        count++;
                    }
                    if (player.isShiftKeyDown() &&
                            !isEncasedShaft(originState) &&
                            !isEncasedCogwheel(originState) &&
                            !player.isCreative() &&
                            originState.getBlock() instanceof IWrenchable)
                        returnItem(player, targetItem, count);
                }
                case COMMON_CASING, PIPE_CASING, CHUTE_CASING -> {
                    if(player.isShiftKeyDown()) return 0;
                    for (BlockPos pos : positions) {
                        BlockState state = level.getBlockState(pos);
                        if(!tryToEncaseAllType(state, level, pos, player, hand, heldItem)) return 0;
                    }
                    count++;
                }
            }
            return count;
        }));
    }

    //仅用于兼容FTB的wrench方法，其他地方放请勿调用
    private static void ftbCompatHandleWrench(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack heldItem) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof IWrenchable wrenchable) {

            UseOnContext useOnContext = new UseOnContext(level,
                    player,
                    hand,
                    heldItem,
                    centerHit(pos, Direction.UP));

            if (player.isShiftKeyDown()) {
                level.levelEvent(2001, pos, Block.getId(state));
                wrenchable.onSneakWrenched(state, useOnContext);

            } else {
                wrenchable.onWrenched(state, useOnContext);

            }
        }
    }

    private static void returnItem(Player player, Item item, int count){
        if(!player.isCreative() && !player.isSpectator()) {
            ItemStack stack = new ItemStack(item, count);
            player.addItem(stack);
        }
    }
}
