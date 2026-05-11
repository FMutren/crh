package top.fmutren.crh.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;
import top.fmutren.crh.Crh;
import top.fmutren.crh.server.ServerPayloadHandler;

public final class ModMessages {

    private static final String NETWORK_VERSION = "1";

    private ModMessages() {
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);

        registrar.playToServer(
                PipeConnectionPayload.TYPE,
                PipeConnectionPayload.STREAM_CODEC,
                ServerPayloadHandler::handlePipeConnection
        );

        registrar.playToServer(
                ChainKeyStatePayload.TYPE,
                ChainKeyStatePayload.STREAM_CODEC,
                ServerPayloadHandler::handleChainKeyState
        );
    }

    public record PipeConnectionPayload(
            BlockPos pos,
            Direction face,
            boolean offHand,
            boolean shift
    ) implements CustomPacketPayload {

        public static final Type<PipeConnectionPayload> TYPE = new Type<>(Crh.id("pipe_connection"));

        public static final StreamCodec<ByteBuf, PipeConnectionPayload> STREAM_CODEC = StreamCodec.composite(
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

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

    public record ChainKeyStatePayload(
            boolean down
    ) implements CustomPacketPayload {

        public static final Type<ChainKeyStatePayload> TYPE = new Type<>(Crh.id("chain_key_state"));

        public static final StreamCodec<ByteBuf, ChainKeyStatePayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                ChainKeyStatePayload::down,
                ChainKeyStatePayload::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}
