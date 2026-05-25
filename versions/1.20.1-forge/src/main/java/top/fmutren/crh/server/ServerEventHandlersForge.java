package top.fmutren.crh.server;

import net.minecraftforge.event.entity.player.PlayerEvent;

public final class ServerEventHandlersForge {

    private ServerEventHandlersForge() {
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerEventHandlers.onPlayerLoggedOut(event.getEntity());
    }

}
