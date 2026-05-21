package top.fmutren.crh.render;

import net.createmod.catnip.ghostblock.GhostBlockParams;
import net.createmod.catnip.ghostblock.GhostBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class OuterContourRender {

    private OuterContourRender(){
    }

    public static void renderGhostBlock(BlockPos pos, BlockState state){
        Object slot = "crh_view";
        int ttl = 1;
        GhostBlockParams params = GhostBlocks.getInstance()
                .showGhostState(slot, state, ttl);
        params.alpha((float) GhostBlocks.getBreathingAlpha());
        params.at(pos);
    }
}
