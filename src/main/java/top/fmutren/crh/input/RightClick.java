package top.fmutren.crh.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.lwjgl.glfw.GLFW;
import top.fmutren.crh.Config;
import top.fmutren.crh.api.CrhServices;
import top.fmutren.crh.network.PipeConnectionMessage;

import static top.fmutren.crh.Config.enableEmptyHandModifyPipe;

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
            ItemStack stack
    ) {
        if (!enableEmptyHandModifyPipe() || !Config.builtinChainAllowed()) {
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
        if (!(state.getBlock() instanceof FluidPipeBlock) && !(state.getBlock() instanceof EncasedPipeBlock)) {
            return false;
        }

        CrhServices.network().sendToServer(new PipeConnectionMessage(
                pos,
                face,
                hand == InteractionHand.OFF_HAND,
                player.isShiftKeyDown()
        ));
        return true;
    }

}
