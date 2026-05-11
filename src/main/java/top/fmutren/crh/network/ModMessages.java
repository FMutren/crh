package top.fmutren.crh.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class ModMessages {

    public record EncasingNetWork(
            BlockPos pos,
            Direction face,
            int hand,
            int Alt
    ) implements CustomPacketPayload {

        public static final Type<EncasingNetWork> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("crh", "encase_data"));

        public static final StreamCodec<ByteBuf, EncasingNetWork> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                EncasingNetWork::pos,
                Direction.STREAM_CODEC,
                EncasingNetWork::face,
                ByteBufCodecs.VAR_INT,
                EncasingNetWork::hand,
                ByteBufCodecs.VAR_INT,
                EncasingNetWork::Alt,
                EncasingNetWork::new
        );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}

