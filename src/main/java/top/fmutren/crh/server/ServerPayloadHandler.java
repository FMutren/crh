package top.fmutren.crh.server;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import top.fmutren.crh.interaction.ChainInteraction;
import top.fmutren.crh.interaction.util.ChainKeyStateTracker;
import top.fmutren.crh.network.ChainKeyStateMessage;
import top.fmutren.crh.network.PipeConnectionMessage;

public final class ServerPayloadHandler {

    private ServerPayloadHandler() {
    }

    public static void handlePipeConnection(PipeConnectionMessage packet, Player player) {
        if (packet == null || player == null) {
            return;
        }
        var hand = packet.offHand() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ChainInteraction.tryTogglePipeConnection(
                player,
                player.level(),
                packet.pos(),
                packet.face(),
                hand,
                packet.shift()
        );
    }

    public static void handleChainKeyState(ChainKeyStateMessage packet, Player player) {
        if (packet == null || player == null) {
            return;
        }
        ChainKeyStateTracker.set(player, packet.down());
    }

}
