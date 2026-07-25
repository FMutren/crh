package top.fmutren.crh.compat.ftbultimine;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;

import static com.simibubi.create.content.equipment.wrench.IWrenchable.playRemoveSound;
import static top.fmutren.crh.interaction.StateSwitch.iterationTypeForItem;
import static top.fmutren.crh.interaction.TryToEncase.tryToEncaseAllType;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;


public class FTBRightClickHandle {
    public static void FTBRightClickEventHandler() {
        RegisterRightClickHandlerEvent.REGISTER.register(registry ->
                registry.registerHandler((context,
                                       hand,
                                       positions) ->
        {
            Player player = context.player();
            if(player.isSpectator() || !player.mayBuild()) return 0;
            Direction face = context.face();
            Level level = player.level();
            ItemStack heldItem = player.getItemInHand(hand);

            int count = 0;

            switch (iterationTypeForItem(heldItem)) {
                case WRENCH -> {
                    for (BlockPos pos : positions) {
                        BlockState state = level.getBlockState(pos);
                        if (state.getBlock() instanceof IWrenchable wrenchable) {
                            UseOnContext useOnContext = new UseOnContext(level,
                                    player,
                                    hand,
                                    heldItem,
                                    centerHit(pos, face.getOpposite()));
                            if (player.isShiftKeyDown()) {
                                onSneakWrenched(state, useOnContext);
                            } else {
                                wrenchable.onWrenched(state, useOnContext);
                            }
                        }
                        count++;
                    }
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

    public static InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        {
            Level world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            Player player = context.getPlayer();

            if (world instanceof ServerLevel serverLevel) {

                BlockState blockState = serverLevel.getBlockState(pos);
                new BlockEvent.BreakEvent(world, pos, blockState, player);

                if (player != null && !player.isCreative()) {
                    Block.getDrops(state, serverLevel, pos, world.getBlockEntity(pos), player, context.getItemInHand())
                            .forEach(itemStack -> {
                                player.getInventory()
                                        .placeItemBackInInventory(itemStack);
                            });
                }

                state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, true);
                world.destroyBlock(pos, false);
                playRemoveSound(world, pos);
            }
            return InteractionResult.SUCCESS;
        }
    }
}
