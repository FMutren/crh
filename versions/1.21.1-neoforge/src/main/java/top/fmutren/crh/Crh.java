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
import top.fmutren.crh.api.CrhServices;
import top.fmutren.crh.compat.createcasing.CreateCasingBridge;
import top.fmutren.crh.compat.createcasing.CrhCreateCasingCompat;
import top.fmutren.crh.compat.ftbultimine.FtbUltimineCompat;
import top.fmutren.crh.input.ClientEventRegister;
import top.fmutren.crh.platform.CreateBridgeImpl;
import top.fmutren.crh.platform.NetworkBridgeImpl;
import top.fmutren.crh.platform.PlatformBridgeImpl;
import top.fmutren.crh.server.ServerEventHandlersNeoForge;

@Mod(CrhCommon.MODID)
public final class Crh {

    public Crh(IEventBus modBus, ModContainer modContainer) {
        var network = new NetworkBridgeImpl();
        CrhServices.bootstrap(network, new CreateBridgeImpl(), new PlatformBridgeImpl());

        var modList = ModList.get();
        bootstrapCreateCasing(modList.isLoaded("createcasing"));

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modBus.addListener(network::registerPayloads);
        NeoForge.EVENT_BUS.addListener(ServerEventHandlersNeoForge::onPlayerLoggedOut);

        if (modList.isLoaded("ftbultimine")) {
            FtbUltimineCompat.register();
        }

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientEventRegister.register(modBus);
        }
    }

    private static void bootstrapCreateCasing(boolean createCasingLoaded) {
        if (!createCasingLoaded) {
            CrhCreateCasingCompat.bootstrap(null);
            CrhCommon.setCreateCasingLoaded(false);
            return;
        }

        try {
            var clazz = Class.forName("top.fmutren.crh.compat.createcasing.CreateCasingBridgeImpl");
            var bridge = (CreateCasingBridge) clazz.getDeclaredConstructor().newInstance();
            CrhCreateCasingCompat.bootstrap(bridge);
            CrhCommon.setCreateCasingLoaded(bridge.isLoaded());
        } catch (ReflectiveOperationException | LinkageError error) {
            CrhCreateCasingCompat.bootstrap(null);
            CrhCommon.setCreateCasingLoaded(false);
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(CrhCommon.MODID, path);
    }

}
