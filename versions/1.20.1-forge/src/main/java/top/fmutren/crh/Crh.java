package top.fmutren.crh;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import top.fmutren.crh.api.CrhServices;
import top.fmutren.crh.compat.ftbultimine.FtbUltimineCompat;
import top.fmutren.crh.input.ClientEventRegister;
import top.fmutren.crh.platform.CreateBridgeImpl;
import top.fmutren.crh.platform.NetworkBridgeImpl;
import top.fmutren.crh.platform.PlatformBridgeImpl;
import top.fmutren.crh.server.ServerEventHandlersForge;

@SuppressWarnings("removal")
@Mod(CrhCommon.MODID)
public final class Crh {

    public Crh() {
        var network = new NetworkBridgeImpl();
        CrhServices.bootstrap(network, new CreateBridgeImpl(), new PlatformBridgeImpl());

        var modList = ModList.get();
        CrhCommon.setCreateCasingLoaded(modList.isLoaded("createcasing"));

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        network.registerPayloads();
        MinecraftForge.EVENT_BUS.addListener(ServerEventHandlersForge::onPlayerLoggedOut);

        if (modList.isLoaded("ftbultimine")) {
            FtbUltimineCompat.register();
        }

        if (FMLLoader.getDist().isClient()) {
            ClientEventRegister.register();
        }
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(CrhCommon.MODID, path);
    }

}
