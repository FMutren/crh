package top.fmutren.crh;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {

    public static final ModConfigSpec.IntValue MAX_PIPE_BLOCKS;
    public static final ModConfigSpec.IntValue MAX_SHAFT_BLOCKS;
    public static final ModConfigSpec.IntValue MAX_BELT_BLOCKS;
    public static final ModConfigSpec.DoubleValue MAX_EMPTY_HAND_PIPE_REACH;
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

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

}
