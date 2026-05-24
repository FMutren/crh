package top.fmutren.crh.render;

import net.createmod.catnip.ghostblock.GhostBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class OuterContourRender {

    private static int id;

    private OuterContourRender() {
    }

    public static void renderGhostBlock(BlockPos pos, BlockState state) {
        Object slot = "crh_view" + id++;
        GhostBlocks.getInstance()
                .showGhostState(slot, state)
                .at(pos)
                .breathingAlpha();
    }

}
