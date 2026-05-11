package top.fmutren.crh.server;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.fmutren.crh.interaction.ChainInteraction;
import top.fmutren.crh.interaction.util.ChainKeyStateTracker;
import top.fmutren.crh.network.ModMessages;

public final class ServerPayloadHandler {

    private ServerPayloadHandler() {
    }

    public static void handlePipeConnection(
            final ModMessages.PipeConnectionPayload packet,
            final IPayloadContext context
    ) {
        Player player = context.player();
        InteractionHand hand = packet.offHand() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ChainInteraction.tryTogglePipeConnection(
                player,
                player.level(),
                packet.pos(),
                packet.face(),
                hand,
                packet.shift()
        );
    }

    public static void handleChainKeyState(
            final ModMessages.ChainKeyStatePayload packet,
            final IPayloadContext context
    ) {
        ChainKeyStateTracker.set(context.player(), packet.down());
    }

}
