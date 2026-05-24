package top.fmutren.crh.compat.ftbultimine;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import top.fmutren.crh.interaction.util.PredicatesCreator;

import static top.fmutren.crh.Crh.loadCreateCasing;
import static top.fmutren.crh.interaction.StateSwitch.commonSwitchForHeldItem;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isEncasedCogwheel;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isEncasedShaft;

public final class FTBRightClickHandle {

    private FTBRightClickHandle() {
    }

    public static void ftbRightClickEventHandler() {
        RegisterRightClickHandlerEvent.REGISTER.register(registry ->
                registry.registerHandler((context, hand, positions) -> {
                    var player = context.player();

                    if (player.isSpectator() || !player.mayBuild()) {
                        return 0;
                    }

                    var level = player.level();
                    var originPos = context.origPos();
                    var originState = level.getBlockState(originPos);
                    var heldItem = player.getItemInHand(hand);
                    boolean isShift = player.isShiftKeyDown();

                    return switch (commonSwitchForHeldItem(heldItem)) {
                        case 0 -> handleWrench(level, player, hand, heldItem, positions, originState, isShift);
                        case 1, 2 -> isShift ? 0 : handleEncasing(level, player, hand, heldItem, positions);
                        case 3 -> isShift ? 0 : handleChuteEncasing(level, player, positions);
                        default -> 0;
                    };
                })
        );
    }

    private static int handleWrench(
            Level level,
            Player player,
            InteractionHand hand,
            ItemStack heldItem,
            Iterable<BlockPos> positions,
            BlockState originState,
            boolean isShift
    ) {
        int count = 0;
        Item returnItem = getReturnItem(originState);

        for (BlockPos pos : positions) {
            BlockState state = level.getBlockState(pos);

            if (!(state.getBlock() instanceof IWrenchable wrenchable)) {
                continue;
            }

            UseOnContext useOnContext = new UseOnContext(
                    level,
                    player,
                    hand,
                    heldItem,
                    centerHit(pos, Direction.UP)
            );

            if (isShift) {
                level.levelEvent(2001, pos, Block.getId(state));
                wrenchable.onSneakWrenched(state, useOnContext);
            } else {
                wrenchable.onWrenched(state, useOnContext);
            }

            count++;
        }

        if (isShift
                && count > 0
                && !isEncasedShaft(originState)
                && !isEncasedCogwheel(originState)) {
            returnItem(player, returnItem, count);
        }

        return count;
    }

    private static int handleEncasing(
            Level level,
            Player player,
            InteractionHand hand,
            ItemStack heldItem,
            Iterable<BlockPos> positions
    ) {
        int count = 0;

        for (var pos : positions) {
            var state = level.getBlockState(pos);
            var block = state.getBlock();

            switch (block) {
                case EncasableBlock encasableBlock -> {
                    var result = encasableBlock.tryEncase(
                            state,
                            level,
                            pos,
                            heldItem,
                            player,
                            hand,
                            centerHit(pos, Direction.UP)
                    );

                    if (result.consumesAction()) {
                        count++;
                    }
                }
                case BeltBlock beltBlock -> {
                    if (tryEncaseBelt(heldItem, pos, level, beltBlock, player)) {
                        count++;
                    }
                }
                case ChuteBlock ignored -> {
                    if (AllBlocks.INDUSTRIAL_IRON_BLOCK.isIn(heldItem)
                            && loadCreateCasing
                            && tryEncaseChute(level, pos, state, player)) {
                        count++;
                    }
                }
                default -> {
                }
            }
        }

        return count;
    }

    private static int handleChuteEncasing(
            Level level,
            Player player,
            Iterable<BlockPos> positions
    ) {
        int count = 0;

        for (BlockPos pos : positions) {
            var state = level.getBlockState(pos);

            if (!(state.getBlock() instanceof ChuteBlock)) {
                continue;
            }

            if (tryEncaseChute(level, pos, state, player)) {
                count++;
            }
        }

        return count;
    }

    private static boolean tryEncaseBelt(
            ItemStack heldItem,
            BlockPos pos,
            Level level,
            BeltBlock beltBlock,
            Player player
    ) {
        if (AllBlocks.COPPER_CASING.isIn(heldItem) && !loadCreateCasing) {
            return false;
        }

        var casingType = PredicatesCreator.beltCasingType(heldItem);

        if (casingType == null) {
            return false;
        }

        beltBlock.withBlockEntityDo(level, pos, be -> be.setCasingType(casingType));
        beltBlock.updateCoverProperty(level, pos, level.getBlockState(pos));

        playPlacedSound(getCasingSoundBlock(heldItem), pos, level, player);
        return true;
    }

    private static boolean tryEncaseChute(
            Level level,
            BlockPos pos,
            BlockState state,
            Player player
    ) {
        var shape = state.getValue(ChuteBlock.SHAPE);

        if (shape == ChuteBlock.Shape.ENCASED || shape == ChuteBlock.Shape.INTERSECTION) {
            return false;
        }

        var newState = state.setValue(ChuteBlock.SHAPE, ChuteBlock.Shape.ENCASED);
        level.setBlockAndUpdate(pos, newState);
        playPlacedSound(AllBlocks.INDUSTRIAL_IRON_BLOCK, pos, level, player);

        return true;
    }

    private static Item getReturnItem(BlockState originState) {
        if (originState.getBlock() instanceof EncasedPipeBlock) {
            return AllBlocks.FLUID_PIPE.asItem();
        }

        return originState.getBlock().asItem();
    }

    private static BlockEntry<? extends Block> getCasingSoundBlock(ItemStack heldItem) {
        if (AllBlocks.COPPER_CASING.isIn(heldItem)) {
            return AllBlocks.COPPER_CASING;
        }

        if (AllBlocks.BRASS_CASING.isIn(heldItem)) {
            return AllBlocks.BRASS_CASING;
        }

        return AllBlocks.ANDESITE_CASING;
    }

    public static void playPlacedSound(
            BlockEntry<? extends Block> blockEntry,
            BlockPos pos,
            Level level,
            Player player
    ) {
        var soundType = blockEntry.getDefaultState().getSoundType(level, pos, player);

        level.playSound(
                null,
                pos,
                soundType.getPlaceSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F
        );
    }

    public static void returnItem(Player player, Item item, int count) {
        if (count <= 0 || player.isCreative() || player.isSpectator()) {
            return;
        }

        var stack = new ItemStack(item, count);
        player.addItem(stack);
    }

}
