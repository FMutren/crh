package top.fmutren.crh.compat.ftbultimine;

import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class WrenchChainGuard {

    private static final Map<UUID, Integer> DEPTH = new ConcurrentHashMap<>();

    private WrenchChainGuard() {
    }

    public static boolean isActive(Player player) {
        return player != null && DEPTH.containsKey(player.getUUID());
    }

    public static void run(Player player, Runnable action) {
        if (player == null) {
            action.run();
            return;
        }

        DEPTH.merge(player.getUUID(), 1, Integer::sum);
        try {
            action.run();
        } finally {
            DEPTH.compute(player.getUUID(),
                    (id, depth) -> depth == null || depth <= 1 ? null : depth - 1);
        }
    }
}
