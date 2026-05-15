package top.fmutren.crh.server;

import net.minecraftforge.event.entity.player.PlayerEvent;
import top.fmutren.crh.interaction.util.ChainKeyStateTracker;

public final class ServerEventHandlers {

    private ServerEventHandlers() {
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        ChainKeyStateTracker.clear(event.getEntity());
    }

}
