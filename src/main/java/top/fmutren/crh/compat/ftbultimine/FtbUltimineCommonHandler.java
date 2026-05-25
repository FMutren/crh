package top.fmutren.crh.compat.ftbultimine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import top.fmutren.crh.api.CrhServices;

import static top.fmutren.crh.interaction.StateSwitch.commonSwitchForHeldItem;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;

public final class FtbUltimineCommonHandler {

    private FtbUltimineCommonHandler() {
    }

    public static int handle(
            Player player,
            InteractionHand hand,
            Iterable<BlockPos> positions,
            BlockPos originPos
    ) {
        if (player == null || hand == null || positions == null || originPos == null) {
            return 0;
        }

        if (player.isSpectator() || !player.mayBuild()) {
            return 0;
        }

        var level = player.level();
        var originState = level.getBlockState(originPos);
        var heldItem = player.getItemInHand(hand);
        boolean isShift = player.isShiftKeyDown();

        return switch (commonSwitchForHeldItem(heldItem)) {
            case 0 -> handleWrench(level, player, hand, heldItem, positions, originState, isShift);
            case 1, 2 -> isShift ? 0 : handleEncasing(level, player, hand, heldItem, positions);
            case 3 -> isShift ? 0 : handleChuteEncasing(level, player, positions);
            default -> 0;
        };
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

        for (BlockPos pos : positions) {
            BlockState state = level.getBlockState(pos);

            UseOnContext useOnContext = new UseOnContext(
                    level,
                    player,
                    hand,
                    heldItem,
                    centerHit(pos, Direction.UP)
            );

            var result = CrhServices.create().applyWrench(state, useOnContext, isShift);
            if (result.consumesAction()) {
                count++;
            }
        }

        if (isShift
                && count > 0
                && !CrhServices.create().isEncasedShaftOrCogwheel(originState)) {
            var item = CrhServices.create().returnItemForState(originState);
            if (!player.isCreative() && !player.isSpectator()) {
                player.addItem(new ItemStack(item, count));
            }
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

            var result = CrhServices.create().tryEncase(
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
                continue;
            }

            if (CrhServices.create().isBelt(state)) {
                if (CrhServices.create().tryBeltCasingFromItem(heldItem, level, pos, state, player)) {
                    count++;
                }
            } else if (CrhServices.create().isChute(state)) {
                if (CrhServices.create().tryChuteEncasingWithItem(heldItem, level, pos, state, player)) {
                    count++;
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

            if (CrhServices.create().tryApplyChuteEncasing(level, pos, state, player)) {
                count++;
            }
        }

        return count;
    }

}
