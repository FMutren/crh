package top.fmutren.crh.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import top.fmutren.crh.network.ModMessages;

@OnlyIn(Dist.CLIENT)
public class RightClick {

    public static final Lazy<KeyMapping> ENCASE_MAPPING = Lazy.of(() ->
            new KeyMapping(
                    "key.crh.encasing",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT_ALT,
                    "key.categories.crh.encasing"
            )
    );

    @SubscribeEvent
    public static void RightClickEvent(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (!level.isClientSide) return;
        InteractionHand hand = event.getHand();
        if (!KeyDown.rightClickPressed) {
            KeyDown.rightClickPressed = true;

            int handN = hand == InteractionHand.OFF_HAND ? 1 : 0;
            int isAlt = ENCASE_MAPPING.get().isDown() ? 1 : 0;

            // 发送包到服务端
            PacketDistributor.sendToServer(new ModMessages.EncasingNetWork(event.getPos(), event.getFace(), handN, isAlt));
        }
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(ENCASE_MAPPING.get());
    }

}
