package top.fmutren.crh.input;

import net.minecraft.world.entity.player.Player;
import top.fmutren.crh.Config;
import top.fmutren.crh.interaction.util.ChainKeyStateTracker;
import top.fmutren.crh.network.ModMessages;
import top.fmutren.crh.network.packet.ClickNetWorkHandle;

import static top.fmutren.crh.input.RightClick.ENCASE_MAPPING;

public final class KeyDown {

    private static boolean lastSentChainKeyState;

    private KeyDown() {}

    public static void syncChainKeyState(Player player) {
        if (!Config.builtinChainAllowed()) {
            ChainKeyStateTracker.set(player, false);
            if (lastSentChainKeyState) {
                ModMessages.sendToServer(new ClickNetWorkHandle(false));
                lastSentChainKeyState = false;
            }
            return;
        }

        boolean chainKeyDown = ENCASE_MAPPING.get().isDown();
        ChainKeyStateTracker.set(player, chainKeyDown);

        if (chainKeyDown == lastSentChainKeyState) return;

        ModMessages.sendToServer(new ClickNetWorkHandle(chainKeyDown));
        lastSentChainKeyState = chainKeyDown;
    }
}
