package top.fmutren.crh.interaction.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.TickPriority;
import top.fmutren.crh.api.CrhServices;
import top.fmutren.crh.interaction.ChainInteraction;
import top.fmutren.crh.interaction.ChainSelection;

public final class ChainOperation {

    private ChainOperation() {
    }

    public static ChainInteraction.ChainOperationResult applyEncasing(
            Level level,
            Player player,
            InteractionHand hand,
            BlockHitResult originalHit,
            ItemStack stack,
            ChainSelection selection
    ) {
        if (level == null || player == null || hand == null || originalHit == null
                || stack == null || selection == null || selection.isEmpty()) {
            return new ChainInteraction.ChainOperationResult(player, hand, selection, 0);
        }

        int changed = 0;
        boolean beltCasingItem = CrhServices.create().isBeltCasingItem(stack);
        boolean creative = player.getAbilities().instabuild;
        Direction face = originalHit.getDirection();

        for (BlockPos targetPos : selection.positions()) {
            if (!creative && stack.isEmpty()) {
                break;
            }

            if (!level.isLoaded(targetPos)) {
                continue;
            }

            BlockState current = level.getBlockState(targetPos);

            if (beltCasingItem && CrhServices.create().isBelt(current)) {
                if (CrhServices.create().tryBeltCasingFromItem(stack, level, targetPos, current, player)) {
                    changed++;
                }
                continue;
            }

            var result = CrhServices.create().tryEncase(
                    current,
                    level,
                    targetPos,
                    stack,
                    player,
                    hand,
                    centerHit(targetPos, face)
            );

            if (result.consumesAction()) {
                changed++;
            }
        }

        return new ChainInteraction.ChainOperationResult(player, hand, selection, changed);
    }

    public static BlockHitResult centerHit(BlockPos pos, Direction face) {
        return new BlockHitResult(Vec3.atCenterOf(pos), face, pos, false);
    }

    public static ChainInteraction.ChainOperationResult applyBelt(
            Level level,
            Player player,
            InteractionHand hand,
            ItemStack stack,
            ChainSelection selection
    ) {
        if (!CrhServices.create().isBeltCasingItem(stack) || selection == null || selection.isEmpty()) {
            return new ChainInteraction.ChainOperationResult(player, hand, selection, 0);
        }

        int changed = 0;
        boolean creative = player.getAbilities().instabuild;

        for (BlockPos targetPos : selection.positions()) {
            if (!creative && stack.isEmpty()) {
                break;
            }

            if (!level.isLoaded(targetPos)) {
                continue;
            }

            BlockState current = level.getBlockState(targetPos);

            if (!CrhServices.create().isBelt(current)) {
                continue;
            }

            if (CrhServices.create().tryBeltCasingFromItem(stack, level, targetPos, current, player)) {
                changed++;
            }
        }

        return new ChainInteraction.ChainOperationResult(player, hand, selection, changed);
    }

    public static boolean applyPipe(
            Player player,
            Level level,
            BlockPos pos,
            Direction face,
            InteractionHand hand,
            boolean shift
    ) {
        PipeApplyResult result = applyPipeInternal(player, level, pos, face, hand, shift, true, true);

        if (result.consumesAction()) {
            player.swing(hand, true);
            return true;
        }

        return false;
    }

    private static PipeApplyResult applyPipeInternal(
            Player player,
            Level level,
            BlockPos pos,
            Direction face,
            InteractionHand hand,
            boolean shift,
            boolean checkReach,
            boolean showMessage
    ) {
        if (player == null || level == null || pos == null || face == null || hand == null) {
            return PipeApplyResult.PASS;
        }

        if (level.isClientSide || player.isSpectator() || !player.mayBuild()) {
            return PipeApplyResult.PASS;
        }

        if (!player.getItemInHand(hand).isEmpty()) {
            return PipeApplyResult.PASS;
        }

        if (!level.isLoaded(pos)) {
            return PipeApplyResult.PASS;
        }

        if (checkReach && player.distanceToSqr(Vec3.atCenterOf(pos)) > CrhServices.platform()
                .maxEmptyHandPipeReachSqr()) {
            return PipeApplyResult.PASS;
        }

        BlockState state = level.getBlockState(pos);
        if (!PredicatesCreator.isManualPipe(state)) {
            return PipeApplyResult.PASS;
        }

        Direction targetFace = shift ? face.getOpposite() : face;
        BooleanProperty property = PredicatesCreator.pipeProperty(targetFace);
        if (property == null || !state.hasProperty(property)) {
            return PipeApplyResult.PASS;
        }

        boolean current = state.getValue(property);

        if (current && CrhServices.create().isPlainFluidPipe(state)
                && PredicatesCreator.countOpenPipeFaces(state) <= 2) {
            if (showMessage) {
                player.displayClientMessage(Component.translatable("crh.message.pipelowerthantwo")
                        .withStyle(ChatFormatting.RED), true);
            }

            return PipeApplyResult.CONSUMED;
        }

        BlockState updated = state.setValue(property, !current);

        CrhServices.create().beforePipeStateChange(level, pos);
        level.setBlock(pos, updated, Block.UPDATE_ALL);
        level.scheduleTick(pos, updated.getBlock(), 1, TickPriority.HIGH);
        CrhServices.create().afterPipeStateChange(level, pos);

        level.playSound(null, pos, SoundEvents.COPPER_PLACE, SoundSource.BLOCKS, 0.6f, 1.2f);

        return PipeApplyResult.CHANGED;
    }

    public static ChainInteraction.ChainOperationResult applyWrench(
            UseOnContext originalContext,
            ChainSelection selection
    ) {
        var player = originalContext.getPlayer();
        var level = originalContext.getLevel();
        InteractionHand hand = originalContext.getHand();

        if (player == null || selection == null || selection.isEmpty()) {
            return new ChainInteraction.ChainOperationResult(player, hand, selection, 0);
        }

        Direction clickedFace = originalContext.getClickedFace();
        boolean sneaking = player.isShiftKeyDown();

        int changed = 0;

        for (BlockPos targetPos : selection.positions()) {
            if (!level.isLoaded(targetPos)) {
                continue;
            }

            BlockState current = level.getBlockState(targetPos);

            var targetContext = new UseOnContext(
                    player,
                    hand,
                    centerHit(targetPos, clickedFace)
            );

            var result = CrhServices.create().applyWrench(current, targetContext, sneaking);
            if (result.consumesAction()) {
                changed++;
            }
        }

        return new ChainInteraction.ChainOperationResult(player, hand, selection, changed);
    }

    private enum PipeApplyResult {

        PASS,
        CONSUMED,
        CHANGED;

        boolean consumesAction() {
            return this == CONSUMED || this == CHANGED;
        }

        boolean changed() {
            return this == CHANGED;
        }

    }

}
