package top.fmutren.crh.server;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class ServerEventHandlersNeoForge {

    private ServerEventHandlersNeoForge() {
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerEventHandlers.onPlayerLoggedOut(event.getEntity());
    }

}
