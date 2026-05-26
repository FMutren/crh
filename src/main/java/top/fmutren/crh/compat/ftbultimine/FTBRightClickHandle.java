package top.fmutren.crh.compat.ftbultimine;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import top.fmutren.crh.interaction.util.PredicatesCreator;

import static top.fmutren.crh.Crh.loadCreateCasing;
import static top.fmutren.crh.interaction.StateSwitch.commonSwitchForHeldItem;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isEncasedShaft;


public class FTBRightClickHandle {
    public static void FTBRightClickEventHandler() {
        RegisterRightClickHandlerEvent.REGISTER.register(registry -> {
            registry.registerHandler((context,
                                      hand,
                                      positions) ->
            {
                Player player = context.player();
                if(player.isSpectator() || !player.mayBuild()) return 0;
                Level level = player.level();
                ItemStack heldItem = player.getItemInHand(hand);
                BlockState originState = level.getBlockState(context.origPos());

                int count = 0;

                switch (commonSwitchForHeldItem(heldItem)) {
                    case 0 -> {

                        Item item = originState.getBlock().asItem();
                        if (level.getBlockState(context.origPos()).getBlock() instanceof EncasedPipeBlock)
                            item = AllBlocks.FLUID_PIPE.asItem();

                        for (BlockPos pos : positions) {
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
                            count++;
                        }
                        if (player.isShiftKeyDown()&& !isEncasedShaft(originState)) returnItem(player, item, count);
                    }
                    case 1, 2 -> {
                        if(player.isShiftKeyDown()) return 0;
                        for (BlockPos pos : positions) {
                            BlockState state = level.getBlockState(pos);
                            switch (state.getBlock()) {
                                case EncasableBlock encasableBlock -> {

                                    encasableBlock.tryEncase(
                                            state,
                                            level,
                                            pos,
                                            heldItem,
                                            player,
                                            hand,
                                            centerHit(pos, Direction.UP));

                                }
                                case BeltBlock beltBlock -> {

                                    if (AllBlocks.COPPER_CASING.isIn(heldItem) && !loadCreateCasing) return 0;

                                    encasingForBelt(heldItem, pos, level, beltBlock);

                                    playPlacedSound(AllBlocks.ANDESITE_CASING, pos, level, player);
                                }

                                case ChuteBlock chuteBlock -> {

                                    if(!AllBlocks.INDUSTRIAL_IRON_BLOCK.isIn(heldItem) || !loadCreateCasing) return 0;

                                    if(state.getValue(ChuteBlock.SHAPE).equals(ChuteBlock.Shape.ENCASED) ||
                                            state.getValue(ChuteBlock.SHAPE).equals(ChuteBlock.Shape.INTERSECTION)) continue;

                                    BlockState newState = state.setValue(ChuteBlock.SHAPE, ChuteBlock.Shape.ENCASED);
                                    level.setBlockAndUpdate(pos, newState);
                                    playPlacedSound(AllBlocks.INDUSTRIAL_IRON_BLOCK, pos, level, player);
                                }
                                default -> throw new IllegalStateException("Unexpected value: " + state.getBlock());
                            }
                            count++;
                            if(state.getBlock() instanceof ChuteBlock) {}
                        }

                    }
                    case 3 ->{
                        if(player.isShiftKeyDown()) return 0;
                        for (BlockPos pos : positions) {
                            BlockState state = level.getBlockState(pos);
                            if(state.getBlock() instanceof ChuteBlock) {
                                if(state.getValue(ChuteBlock.SHAPE).equals(ChuteBlock.Shape.ENCASED) ||
                                        state.getValue(ChuteBlock.SHAPE).equals(ChuteBlock.Shape.INTERSECTION)) continue;

                                BlockState newState = state.setValue(ChuteBlock.SHAPE, ChuteBlock.Shape.ENCASED);
                                level.setBlockAndUpdate(pos, newState);
                                playPlacedSound(AllBlocks.INDUSTRIAL_IRON_BLOCK, pos, level, player);
                            }
                            count++;
                        }
                    }
                }
                return count;
            });
        });
    }

    public static void encasingForBelt(ItemStack heldItem, BlockPos pos, Level level, BeltBlock beltBlock) {
        BeltBlockEntity.CasingType casingType = PredicatesCreator.beltCasingType(heldItem);
        if (casingType == null) return;
        beltBlock.withBlockEntityDo(level, pos, be -> be.setCasingType(casingType));
        beltBlock.updateCoverProperty(level, pos, level.getBlockState(pos));
    }

    public static void playPlacedSound(BlockEntry<? extends Block> blockEntry, BlockPos pos, Level level, Player player) {
        SoundType soundType = blockEntry.getDefaultState()
                .getSoundType(level, pos, player);
        level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
    }

    public static  void returnItem(Player player, Item item, int count){
        if(!player.isCreative() && !player.isSpectator()) {
            ItemStack stack = new ItemStack(item, count);
            player.addItem(stack);
        }
    }
}
