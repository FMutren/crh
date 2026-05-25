package top.fmutren.crh.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.lwjgl.glfw.GLFW;
import top.fmutren.crh.api.CrhServices;
import top.fmutren.crh.interaction.ChainInteraction;
import top.fmutren.crh.interaction.StateSwitch;
import top.fmutren.crh.mixinhook.CreateEncasingHooks;
import top.fmutren.crh.network.PipeConnectionMessage;

public final class RightClick {

    public static final KeyMapping ENCASE_MAPPING = new KeyMapping(
            "key.crh.encasing",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            "key.categories.crh.encasing"
    );

    private RightClick() {
    }

    public static boolean handleRightClickBlock(
            Level level,
            BlockPos pos,
            Direction face,
            InteractionHand hand,
            Player player,
            ItemStack stack,
            BlockHitResult hitResult
    ) {
        if (handleEmptyHandPipe(level, pos, face, hand, player, stack)) {
            return true;
        }

        if (level == null || pos == null || hand == null || player == null || stack == null || stack.isEmpty()
                || hitResult == null || !ChainInteraction.canUseChain(player)) {
            return false;
        }

        // Let the vanilla client packet reach the server; server-side event handling performs the mutation.
        if (level.isClientSide) {
            return false;
        }

        var state = level.getBlockState(pos);

        if (StateSwitch.isCreateWrench(stack)) {
            InteractionResult result = ChainInteraction.tryHandleWrench(new UseOnContext(player, hand, hitResult));
            return result.consumesAction();
        }

        var encasingResult = CreateEncasingHooks.tryHandleEncasing(stack, state, level, pos, player, hand, hitResult);
        if (encasingResult.consumesAction()) {
            return true;
        }

        return CreateEncasingHooks.tryEncaseAfterPlaceInShaft(stack, state, level, pos, player, hand, hitResult);
    }

    private static boolean handleEmptyHandPipe(
            Level level,
            BlockPos pos,
            Direction face,
            InteractionHand hand,
            Player player,
            ItemStack stack
    ) {
        if (!CrhServices.platform().enableEmptyHandModifyPipe() || !CrhServices.platform().builtinChainEnabled()) {
            return false;
        }

        if (level == null || pos == null || face == null || hand == null || player == null || stack == null) {
            return false;
        }

        if (!level.isClientSide) {
            return false;
        }

        if (!stack.isEmpty()) {
            return false;
        }

        var state = level.getBlockState(pos);
        if (!CrhServices.create().isManualPipe(state)) {
            return false;
        }

        CrhServices.network().sendPipeConnection(new PipeConnectionMessage(
                pos,
                face,
                hand == InteractionHand.OFF_HAND,
                player.isShiftKeyDown()
        ));
        return true;
    }

}
