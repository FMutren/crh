package top.fus_lingx.crh.crh;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class AltDown{
    private static boolean altDown = false;
    public static int tick = 0;

    @SubscribeEvent
    public static void TickEvent(PlayerTickEvent.Post event) {//按住alt提示（仅扳手和机壳生效
        Player player = event.getEntity();
        boolean isAltDown = Screen.hasAltDown();
        if (isAltDown && !altDown) {
            if(player.getMainHandItem().getItem().toString().equals("create:wrench")){
                player.displayClientMessage(Component.translatable("fus.message.altdownwithwrench"), true);
            }
            else if (player.getMainHandItem().getItem().toString().equals("create:andesite_casing") || player.getMainHandItem().getItem().toString().equals("create:brass_casing") || player.getMainHandItem().getItem().toString().equals("create:copper_casing")) {
                player.displayClientMessage(Component.translatable("fus.message.altdownwithcasing"), true);
            }
        }
        altDown = isAltDown;
    }
}
