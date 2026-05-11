package top.fmutren.crh;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import top.fmutren.crh.input.ClientEventRegister;
import top.fmutren.crh.network.ModMessages;
import top.fmutren.crh.server.ServerEventHandlers;

@Mod(Crh.MODID)
public final class Crh {

    public static final String MODID = "crh";

    public Crh(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modBus.addListener(ModMessages::registerPayloads);
        NeoForge.EVENT_BUS.addListener(ServerEventHandlers::onPlayerLoggedOut);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientEventRegister.register(modBus);
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

}
