package top.fmutren.crh.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import top.fmutren.crh.interaction.util.ChainKeyStateTracker;

import java.util.function.Supplier;

public class ClickNetWorkHandle {
    private final boolean down;

    public ClickNetWorkHandle(boolean down) {
        this.down = down;
    }

    public static void encode(ClickNetWorkHandle pkt, FriendlyByteBuf buf) {
        buf.writeBoolean(pkt.down);
    }

    public static ClickNetWorkHandle decode(FriendlyByteBuf buf) {
        return new ClickNetWorkHandle(buf.readBoolean());
    }

    public static void handle(ClickNetWorkHandle pkt, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player != null) {
            ChainKeyStateTracker.set(player, pkt.down);
        }
    }
}
