package top.fmutren.crh;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import top.fmutren.crh.compat.ftbultimine.FTBUltimineCompat;
import top.fmutren.crh.input.ClientEventRegister;
import top.fmutren.crh.network.ModMessages;
import top.fmutren.crh.server.ServerEventHandlers;

@Mod(Crh.MODID)
public final class Crh {

    public static final String MODID = "crh";

    public static boolean loadCreateCasing = false;

    public Crh(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modBus.addListener(ModMessages::registerPayloads);
        NeoForge.EVENT_BUS.addListener(ServerEventHandlers::onPlayerLoggedOut);

        if (ModList.get().isLoaded("ftbultimine")) {
            FTBUltimineCompat.register();
        }

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientEventRegister.register(modBus);
        }

        if (ModList.get().isLoaded("createcasing")) loadCreateCasing = true;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

}
