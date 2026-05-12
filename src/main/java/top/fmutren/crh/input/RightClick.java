package top.fmutren.crh.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import top.fmutren.crh.network.ModMessages;

public final class RightClick {

    public static final Lazy<KeyMapping> ENCASE_MAPPING = Lazy.of(() ->
            new KeyMapping(
                    "key.crh.encasing",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT_ALT,
                    "key.categories.crh.encasing"
            )
    );

    private RightClick() {
    }

    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (!level.isClientSide) return;

        ItemStack stack = event.getItemStack();
        if (!stack.isEmpty()) return;

        BlockState state = level.getBlockState(event.getPos());
        if (!(state.getBlock() instanceof FluidPipeBlock) && !(state.getBlock() instanceof EncasedPipeBlock)) return;

        InteractionHand hand = event.getHand();
        PacketDistributor.sendToServer(new ModMessages.PipeConnectionPayload(
                event.getPos(),
                event.getFace(),
                hand == InteractionHand.OFF_HAND,
                event.getEntity().isShiftKeyDown()
        ));

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(ENCASE_MAPPING.get());
    }

}
