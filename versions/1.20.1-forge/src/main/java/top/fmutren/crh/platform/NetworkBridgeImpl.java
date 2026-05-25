package top.fmutren.crh.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import top.fmutren.crh.CrhCommon;
import top.fmutren.crh.api.NetworkBridge;
import top.fmutren.crh.network.ChainKeyStateMessage;
import top.fmutren.crh.network.PipeConnectionMessage;
import top.fmutren.crh.server.ServerPayloadHandler;

import java.util.function.Supplier;

@SuppressWarnings("removal")
public final class NetworkBridgeImpl implements NetworkBridge {

    private static final String NETWORK_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(CrhCommon.MODID, "main"))
            .networkProtocolVersion(() -> NETWORK_VERSION)
            .clientAcceptedVersions(NETWORK_VERSION::equals)
            .serverAcceptedVersions(NETWORK_VERSION::equals)
            .simpleChannel();

    public void registerPayloads() {
        int id = 0;
        CHANNEL.messageBuilder(PipeConnectionMessage.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(NetworkBridgeImpl::encodePipeConnection)
                .decoder(NetworkBridgeImpl::decodePipeConnection)
                .consumerMainThread(NetworkBridgeImpl::handlePipeConnection)
                .add();
        CHANNEL.messageBuilder(ChainKeyStateMessage.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(NetworkBridgeImpl::encodeChainKeyState)
                .decoder(NetworkBridgeImpl::decodeChainKeyState)
                .consumerMainThread(NetworkBridgeImpl::handleChainKeyState)
                .add();
    }

    private static void encodePipeConnection(
            PipeConnectionMessage packet,
            FriendlyByteBuf buf
    ) {
        buf.writeBlockPos(packet.pos());
        buf.writeEnum(packet.face());
        buf.writeBoolean(packet.offHand());
        buf.writeBoolean(packet.shift());
    }

    private static PipeConnectionMessage decodePipeConnection(
            FriendlyByteBuf buf
    ) {
        BlockPos pos = buf.readBlockPos();
        Direction face = buf.readEnum(Direction.class);
        boolean offHand = buf.readBoolean();
        boolean shift = buf.readBoolean();
        return new PipeConnectionMessage(pos, face, offHand, shift);
    }

    private static void handlePipeConnection(
            PipeConnectionMessage packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        var context = contextSupplier.get();
        var player = context.getSender();
        if (player != null) {
            context.enqueueWork(() -> ServerPayloadHandler.handlePipeConnection(packet, player));
        }
        context.setPacketHandled(true);
    }

    private static void encodeChainKeyState(
            ChainKeyStateMessage packet,
            FriendlyByteBuf buf
    ) {
        buf.writeBoolean(packet.down());
    }

    private static ChainKeyStateMessage decodeChainKeyState(
            FriendlyByteBuf buf
    ) {
        return new ChainKeyStateMessage(buf.readBoolean());
    }

    private static void handleChainKeyState(
            ChainKeyStateMessage packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        var context = contextSupplier.get();
        var player = context.getSender();
        if (player != null) {
            context.enqueueWork(() -> ServerPayloadHandler.handleChainKeyState(packet, player));
        }
        context.setPacketHandled(true);
    }

    @Override
    public void sendPipeConnection(PipeConnectionMessage message) {
        CHANNEL.sendToServer(message);
    }

    @Override
    public void sendChainKeyState(ChainKeyStateMessage message) {
        CHANNEL.sendToServer(message);
    }

}
