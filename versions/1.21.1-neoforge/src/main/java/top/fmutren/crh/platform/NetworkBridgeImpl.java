package top.fmutren.crh.platform;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;
import top.fmutren.crh.Crh;
import top.fmutren.crh.api.NetworkBridge;
import top.fmutren.crh.network.ChainKeyStateMessage;
import top.fmutren.crh.network.PipeConnectionMessage;
import top.fmutren.crh.server.ServerPayloadHandler;

public final class NetworkBridgeImpl implements NetworkBridge {

    private static final String NETWORK_VERSION = "1";

    public void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);

        registrar.playToServer(
                PipeConnectionPayload.TYPE,
                PipeConnectionPayload.STREAM_CODEC,
                NetworkBridgeImpl::handlePipeConnection
        );

        registrar.playToServer(
                ChainKeyStatePayload.TYPE,
                ChainKeyStatePayload.STREAM_CODEC,
                NetworkBridgeImpl::handleChainKeyState
        );
    }

    private static void handlePipeConnection(PipeConnectionPayload packet, IPayloadContext context) {
        ServerPayloadHandler.handlePipeConnection(packet.toCommon(), context.player());
    }

    private static void handleChainKeyState(ChainKeyStatePayload packet, IPayloadContext context) {
        ServerPayloadHandler.handleChainKeyState(packet.toCommon(), context.player());
    }

    @Override
    public void sendPipeConnection(PipeConnectionMessage message) {
        PacketDistributor.sendToServer(new PipeConnectionPayload(
                message.pos(),
                message.face(),
                message.offHand(),
                message.shift()
        ));
    }

    @Override
    public void sendChainKeyState(ChainKeyStateMessage message) {
        PacketDistributor.sendToServer(new ChainKeyStatePayload(message.down()));
    }

    private record PipeConnectionPayload(
            BlockPos pos,
            Direction face,
            boolean offHand,
            boolean shift
    ) implements CustomPacketPayload {

        private static final Type<PipeConnectionPayload> TYPE = new Type<>(Crh.id("pipe_connection"));

        private static final StreamCodec<ByteBuf, PipeConnectionPayload> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                PipeConnectionPayload::pos,
                Direction.STREAM_CODEC,
                PipeConnectionPayload::face,
                ByteBufCodecs.BOOL,
                PipeConnectionPayload::offHand,
                ByteBufCodecs.BOOL,
                PipeConnectionPayload::shift,
                PipeConnectionPayload::new
        );

        private PipeConnectionMessage toCommon() {
            return new PipeConnectionMessage(pos, face, offHand, shift);
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

    private record ChainKeyStatePayload(boolean down) implements CustomPacketPayload {

        private static final Type<ChainKeyStatePayload> TYPE = new Type<>(Crh.id("chain_key_state"));

        private static final StreamCodec<ByteBuf, ChainKeyStatePayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                ChainKeyStatePayload::down,
                ChainKeyStatePayload::new
        );

        private ChainKeyStateMessage toCommon() {
            return new ChainKeyStateMessage(down);
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}
