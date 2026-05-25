package top.fmutren.crh.server;

import net.minecraft.world.entity.player.Player;
import top.fmutren.crh.interaction.util.ChainKeyStateTracker;

public final class ServerEventHandlers {

    private ServerEventHandlers() {
    }

    public static void onPlayerLoggedOut(Player player) {
        ChainKeyStateTracker.clear(player);
    }

}
