package top.fmutren.crh.interaction;

import com.simibubi.create.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import top.fmutren.crh.Config;
import top.fmutren.crh.interaction.util.*;

public final class ChainInteraction {

    private ChainInteraction() {
    }

    public static InteractionResult tryHandleEncasing(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (!canUseChain(player) || player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        ChainSelection selection = TargetSelector.selectEncasing(level, pos, state, stack);
        if (selection.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            if (AllBlocks.BELT.has(state) && PredicatesCreator.beltCasingType(stack) != null) {
                return InteractionResult.PASS;
            }
            return InteractionResult.SUCCESS;
        }

        return ChainOperation.applyEncasing(level, player, hand, hitResult, stack, selection)
                .toItemInteractionResult();
    }

    public static boolean canUseChain(Player player) {
        return Config.builtinChainAllowed()
                && player != null
                && !player.isSpectator()
                && player.mayBuild()
                && ChainKeyStateTracker.isDown(player);
    }

    public static InteractionResult tryHandleWrench(UseOnContext context) {
        Player player = context.getPlayer();
        if (!canUseChain(player)) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        boolean sneaking = player.isShiftKeyDown();

        ChainSelection selection = TargetSelector.select(level, pos, state, sneaking);
        if (selection.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        return ChainOperation.applyWrench(context, selection).toInteractionResult();
    }

    public static boolean tryTogglePipeConnection(
            Player player,
            Level level,
            BlockPos pos,
            Direction face,
            InteractionHand hand,
            boolean shift
    ) {
        return ChainOperation.applyPipe(player, level, pos, face, hand, shift);
    }

    public record ChainOperationResult(
            Player player,
            InteractionHand hand,
            ChainSelection selection,
            int changed
    ) {

        public InteractionResult toItemInteractionResult() {
            if (!changedAny()) {
                return InteractionResult.PASS;
            }
            InteractionFeedback.finish(player, hand, selection);
            return InteractionResult.SUCCESS;
        }

        public boolean changedAny() {
            return changed > 0;
        }

        public InteractionResult toInteractionResult() {
            if (!changedAny()) {
                return InteractionResult.PASS;
            }
            InteractionFeedback.finish(player, hand, selection);
            return InteractionResult.SUCCESS;
        }

    }

}
