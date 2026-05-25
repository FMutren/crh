package top.fmutren.crh;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import top.fmutren.crh.api.CrhServices;
import top.fmutren.crh.compat.createcasing.CreateCasingCompatLoader;
import top.fmutren.crh.compat.createcasing.CrhCreateCasingCompat;
import top.fmutren.crh.compat.ftbultimine.FtbUltimineCompat;
import top.fmutren.crh.input.ClientEventRegister;
import top.fmutren.crh.input.RightClickEvents;
import top.fmutren.crh.platform.CreateBridgeImpl;
import top.fmutren.crh.platform.NetworkBridgeImpl;
import top.fmutren.crh.platform.PlatformBridgeImpl;
import top.fmutren.crh.server.ServerEventHandlersForge;

@SuppressWarnings("removal")
@Mod(CrhCommon.MODID)
public final class Crh {

    public Crh() {
        var network = new NetworkBridgeImpl();
        var platform = new PlatformBridgeImpl();
        CrhServices.bootstrap(network, new CreateBridgeImpl(), platform);

        bootstrapCreateCasing(platform.isModLoaded("createcasing"));

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        network.registerPayloads();
        MinecraftForge.EVENT_BUS.addListener(ServerEventHandlersForge::onPlayerLoggedOut);
        MinecraftForge.EVENT_BUS.addListener(ServerEventHandlersForge::onEntityPlace);
        MinecraftForge.EVENT_BUS.addListener(RightClickEvents::rightClickBlock);

        if (platform.isModLoaded("ftbultimine")) {
            FtbUltimineCompat.register();
        }

        if (FMLLoader.getDist().isClient()) {
            ClientEventRegister.register();
        }
    }

    private static void bootstrapCreateCasing(boolean createCasingLoaded) {
        var bridge = CreateCasingCompatLoader.load(createCasingLoaded);
        CrhCreateCasingCompat.bootstrap(bridge);
        CrhCommon.setCreateCasingLoaded(bridge.isLoaded());
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(CrhCommon.MODID, path);
    }

}
