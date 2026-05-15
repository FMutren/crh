package top.fmutren.crh.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import top.fmutren.crh.interaction.ChainInteraction;

import java.util.function.Supplier;

public class PipeNetWorkHandle {
    private final BlockPos pos;
    private final Direction face;
    private final InteractionHand hand;
    private final boolean shift;

    public PipeNetWorkHandle(BlockPos pos, Direction face, InteractionHand hand, boolean shift) {
        this.pos = pos;
        this.face = face;
        this.hand = hand;
        this.shift = shift;
    }

    public static void encode(PipeNetWorkHandle pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeEnum(pkt.face);
        buf.writeEnum(pkt.hand);
        buf.writeBoolean(pkt.shift);
    }

    public static PipeNetWorkHandle decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Direction face = buf.readEnum(Direction.class);
        InteractionHand hand = buf.readEnum(InteractionHand.class);
        boolean shift = buf.readBoolean();
        return new PipeNetWorkHandle(pos, face, hand, shift);
    }

    public static void handle(PipeNetWorkHandle pkt, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player != null) {
            ChainInteraction.tryTogglePipeConnection(
                    player,
                    player.level(),
                    pkt.pos,
                    pkt.face,
                    pkt.hand,
                    pkt.shift
            );
        }
    }
}
