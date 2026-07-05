package top.fmutren.crh;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import top.fmutren.crh.event.BlockEventCreator;
import top.fmutren.crh.input.ClientEventRegister;
import top.fmutren.crh.server.ServerEventHandlers;

import static top.fmutren.crh.network.ModMessages.registerPayloads;

@Mod(crh_forge.MODID)
public class crh_forge {
    public static final String MODID = "crh_forge";

    public static boolean loadCreateCasing = false;

    public crh_forge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        MinecraftForge.EVENT_BUS.addListener(ServerEventHandlers::onPlayerLoggedOut);
        MinecraftForge.EVENT_BUS.addListener(BlockEventCreator::onBlockPlace);
        registerPayloads();

        if (FMLEnvironment.dist == Dist.CLIENT) ClientEventRegister.register(modBus);

        if (ModList.get().isLoaded("createcasing")) loadCreateCasing = true;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
