package top.fmutren.crh.interaction.util;

import net.minecraft.world.entity.player.Player;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ChainKeyStateTracker {

    private static final Map<UUID, Boolean> KEY_STATES = new ConcurrentHashMap<>();

    private ChainKeyStateTracker() {
    }

    public static void set(Player player, boolean down) {
        if (player == null) return;
        if (down) {
            KEY_STATES.put(player.getUUID(), true);
        } else {
            KEY_STATES.remove(player.getUUID());
        }
    }

    public static boolean isDown(Player player) {
        return player != null && KEY_STATES.getOrDefault(player.getUUID(), false);
    }

    public static void clear(Player player) {
        if (player != null) {
            KEY_STATES.remove(player.getUUID());
        }
    }

}
