package top.fmutren.crh.input;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import static top.fmutren.crh.input.RightClick.ENCASE_MAPPING;

@OnlyIn(Dist.CLIENT)
public class KeyDown {

    public static boolean rightClickPressed = false;

    @SubscribeEvent
    public static void TickEvent(PlayerTickEvent.Post event) {//按住按键提示（仅扳手和机壳生效
        Player player = event.getEntity();
        rightClickPressed = false;
        if (ENCASE_MAPPING.get().consumeClick()) {
            if (player.getMainHandItem().getItem().toString().equals("create:wrench")) {
                player.displayClientMessage(Component.translatable("fus.message.altdownwithwrench"), true);
            } else if (player.getMainHandItem()
                    .getItem()
                    .toString()
                    .equals("create:andesite_casing") || player.getMainHandItem()
                    .getItem()
                    .toString()
                    .equals("create:brass_casing") || player.getMainHandItem()
                    .getItem()
                    .toString()
                    .equals("create:copper_casing")) {
                player.displayClientMessage(Component.translatable("fus.message.altdownwithcasing"), true);
            }
        }
    }

}
