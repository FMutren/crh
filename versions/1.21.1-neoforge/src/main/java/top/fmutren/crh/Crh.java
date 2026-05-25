package top.fmutren.crh;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import top.fmutren.crh.api.CrhServices;
import top.fmutren.crh.compat.createcasing.CreateCasingCompatLoader;
import top.fmutren.crh.compat.createcasing.CrhCreateCasingCompat;
import top.fmutren.crh.compat.ftbultimine.FtbUltimineCompat;
import top.fmutren.crh.input.ClientEventRegister;
import top.fmutren.crh.input.RightClickEvents;
import top.fmutren.crh.platform.CreateBridgeImpl;
import top.fmutren.crh.platform.NetworkBridgeImpl;
import top.fmutren.crh.platform.PlatformBridgeImpl;
import top.fmutren.crh.server.ServerEventHandlersNeoForge;

@Mod(CrhCommon.MODID)
public final class Crh {

    public Crh(IEventBus modBus, ModContainer modContainer) {
        var network = new NetworkBridgeImpl();
        var platform = new PlatformBridgeImpl();
        CrhServices.bootstrap(network, new CreateBridgeImpl(), platform);

        bootstrapCreateCasing(platform.isModLoaded("createcasing"));

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modBus.addListener(network::registerPayloads);
        NeoForge.EVENT_BUS.addListener(ServerEventHandlersNeoForge::onPlayerLoggedOut);
        NeoForge.EVENT_BUS.addListener(ServerEventHandlersNeoForge::onEntityPlace);
        NeoForge.EVENT_BUS.addListener(RightClickEvents::rightClickBlock);

        if (platform.isModLoaded("ftbultimine")) {
            FtbUltimineCompat.register();
        }

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientEventRegister.register(modBus);
        }
    }

    private static void bootstrapCreateCasing(boolean createCasingLoaded) {
        var bridge = CreateCasingCompatLoader.load(createCasingLoaded);
        CrhCreateCasingCompat.bootstrap(bridge);
        CrhCommon.setCreateCasingLoaded(bridge.isLoaded());
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(CrhCommon.MODID, path);
    }

}
