package top.fmutren.crh;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = crh_forge.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    public static final ForgeConfigSpec.IntValue MAX_PIPE_BLOCKS;
    public static final ForgeConfigSpec.IntValue MAX_SHAFT_BLOCKS;
    public static final ForgeConfigSpec.IntValue MAX_BELT_BLOCKS;
    public static final ForgeConfigSpec.DoubleValue MAX_EMPTY_HAND_PIPE_REACH;
    public static final ForgeConfigSpec.BooleanValue COMPAT_FTB_ULTIMINE;
    public static final ForgeConfigSpec.BooleanValue DISABLE_BUILTIN_CHAIN_WHEN_FTB_ULTIMINE_ENABLED;
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLE_VIEW;
    public static final ForgeConfigSpec.BooleanValue ENABLE_EMPTY_HAND_MODIFY_PIPE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_AUTO_INTERACTION;
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    static {
        BUILDER.push("chain_interaction");

        MAX_PIPE_BLOCKS = BUILDER
                .comment("Maximum connected fluid/encased fluid pipe blocks affected by one chain operation.")
                .defineInRange("maxPipeBlocks", 64, 1, 4096);

        MAX_SHAFT_BLOCKS = BUILDER
                .comment("Maximum shaft or encased shaft blocks affected by one chain operation.")
                .defineInRange("maxShaftBlocks", 128, 1, 4096);

        MAX_BELT_BLOCKS = BUILDER
                .comment("Maximum belt segments affected by one chain operation.")
                .defineInRange("maxBeltBlocks", 128, 1, 4096);

        MAX_EMPTY_HAND_PIPE_REACH = BUILDER
                .comment("Maximum squared block reach allowed for the empty-hand pipe-connection packet.")
                .defineInRange("maxEmptyHandPipeReachSqr", 64.0D, 1.0D, 1024.0D);

        ENABLE_VIEW = BUILDER
                .comment("Enable the chain interaction view overlay.(Will disable with FTB-Ultimine)")
                .define("enableView", true);

        ENABLE_EMPTY_HAND_MODIFY_PIPE = BUILDER
                .comment("Enable empty hand can modify pipe.")
                .define("enableEmptyHandPipe", true);

        ENABLE_AUTO_INTERACTION = BUILDER
                .comment("Enable auto encase or open window for chute or fluid pipe when take casing or wrench by off hand")
                .define("enableAutoInteraction", true);


        COMPAT_FTB_ULTIMINE = BUILDER
                .comment("Enable FTB Ultimine compatibility. When FTB Ultimine is installed, CRH will use Ultimine-selected blocks for chain operations.")
                .define("compatFtbUltimine", true);

        DISABLE_BUILTIN_CHAIN_WHEN_FTB_ULTIMINE_ENABLED = BUILDER
                .comment("Disable CRH built-in chain selection when FTB Ultimine compatibility is active.")
                .define("disableBuiltinChainWhenFtbUltimineCompatEnabled", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private Config() {
    }

    public static int maxPipeBlocks() {
        return MAX_PIPE_BLOCKS.get();
    }

    public static int maxShaftBlocks() {
        return MAX_SHAFT_BLOCKS.get();
    }

    public static int maxBeltBlocks() {
        return MAX_BELT_BLOCKS.get();
    }

    public static double maxEmptyHandPipeReachSqr() {
        return MAX_EMPTY_HAND_PIPE_REACH.get();
    }

    public static boolean enableView() {
        return ENABLE_VIEW.get() && !ModList.get().isLoaded("ftbultimine");
    }

    public static boolean enableEmptyHandModifyPipe() {
        return ENABLE_EMPTY_HAND_MODIFY_PIPE.get();
    }

    public static boolean enableAutoInteraction() {
        return ENABLE_AUTO_INTERACTION.get();
    }

    public static boolean builtinChainAllowed() {
        return !(ftbUltimineCompatActive()
                && disableBuiltinChainWhenFtbUltimineCompatEnabled());
    }

    public static boolean ftbUltimineCompatActive() {
        return compatFtbUltimine() && ModList.get().isLoaded("ftbultimine");
    }

    public static boolean disableBuiltinChainWhenFtbUltimineCompatEnabled() {
        return DISABLE_BUILTIN_CHAIN_WHEN_FTB_ULTIMINE_ENABLED.get();
    }

    public static boolean compatFtbUltimine() {
        return COMPAT_FTB_ULTIMINE.get();
    }

}
