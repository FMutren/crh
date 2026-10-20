package top.fmutren.crh.interaction.util;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import top.fmutren.crh.Config;
import top.fmutren.crh.interaction.ChainInteraction;
import top.fmutren.crh.interaction.ChainSelection;

import static top.fmutren.crh.interaction.TryToEncase.tryToEncase;

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

            if (!(current.getBlock() instanceof EncasableBlock) && !AllBlocks.BELT.has(current)) {
                continue;
            }

            InteractionResult result = tryToEncase(current,
                    level,
                    targetPos,
                    player,
                    hand,
                    stack,
                    face
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

    public static void applyPipe(
            Player player,
            Level level,
            BlockPos pos,
            Direction face,
            InteractionHand hand,
            boolean shift
    ) {
        if (player == null || level == null || pos == null || face == null || hand == null) {
            return;
        }

        if (level.isClientSide || player.isSpectator() || !player.mayBuild()) {
            return;
        }

        if (!player.getItemInHand(hand).isEmpty()) {
            return;
        }

        if (!level.isLoaded(pos) || player.distanceToSqr(Vec3.atCenterOf(pos)) > Config.maxEmptyHandPipeReachSqr()) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (!PredicatesCreator.isManualPipe(state)) {
            return;
        }

        Direction targetFace = shift ? face.getOpposite() : face;
        BooleanProperty property = PredicatesCreator.pipeProperty(targetFace);
        if (property == null || !state.hasProperty(property)) {
            return;
        }

        boolean current = state.getValue(property);
        if (AllBlocks.FLUID_PIPE.has(state) && current && PredicatesCreator.countOpenPipeFaces(state) <= 2) {
            player.displayClientMessage(Component.translatable("crh.message.pipelowerthantwo")
                    .withStyle(ChatFormatting.RED), true);
            return;
        }

        BlockState updated = state.setValue(property, !current);
        FluidTransportBehaviour.cacheFlows(level, pos);
        level.setBlock(pos, updated, Block.UPDATE_ALL);
        level.scheduleTick(pos, updated.getBlock(), 1, TickPriority.HIGH);
        FluidTransportBehaviour.loadFlows(level, pos);
        level.playSound(null, pos, SoundEvents.COPPER_PLACE, SoundSource.BLOCKS, 0.6f, 1.2f);
        player.swing(hand, true);
    }

    public static ChainInteraction.ChainOperationResult applyWrench(
            UseOnContext originalContext, ChainSelection selection) {
        Player player = originalContext.getPlayer();
        Level level = originalContext.getLevel();
        InteractionHand hand = originalContext.getHand();
        Direction clickedFace = originalContext.getClickedFace();

        if (player == null) {
            return new ChainInteraction.ChainOperationResult(null, hand, selection, 0);
        }

        boolean sneaking = player.isShiftKeyDown();

        int changed = 0;
        for (BlockPos targetPos : selection.positions()) {
            if (!level.isLoaded(targetPos)) {
                continue;
            }

            BlockState current = level.getBlockState(targetPos);
            if (!(current.getBlock() instanceof IWrenchable wrenchable)) {
                continue;
            }

            UseOnContext targetContext = new UseOnContext(
                    player,
                    hand,
                    centerHit(targetPos, clickedFace)
            );

            InteractionResult result = sneaking
                    ? wrenchable.onSneakWrenched(current, targetContext)
                    : wrenchable.onWrenched(current, targetContext);

            if (result.consumesAction()) {
                changed++;
            }
        }

        return new ChainInteraction.ChainOperationResult(player, hand, selection, changed);
    }

}
