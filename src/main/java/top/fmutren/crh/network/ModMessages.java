package top.fmutren.crh.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import top.fmutren.crh.crh_forge;
import top.fmutren.crh.network.packet.ClickNetWorkHandle;
import top.fmutren.crh.network.packet.PipeNetWorkHandle;

public class ModMessages {
    private ModMessages() {}
    
    private static SimpleChannel INSTANCE;
    
    private static int packetID = 0;
    
    private static int getID() {return packetID++;}

    public static void registerPayloads() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(crh_forge.MODID, "main"))
                .networkProtocolVersion(() -> "1.0")
                .serverAcceptedVersions(s -> true)
                .clientAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(PipeNetWorkHandle.class, getID(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PipeNetWorkHandle::decode)
                .encoder(PipeNetWorkHandle::encode)
                .consumerMainThread(PipeNetWorkHandle::handle)
                .add();

        net.messageBuilder(ClickNetWorkHandle.class, getID(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ClickNetWorkHandle::decode)
                .encoder(ClickNetWorkHandle::encode)
                .consumerMainThread(ClickNetWorkHandle::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG Message){
        INSTANCE.sendToServer(Message);
    }
}
