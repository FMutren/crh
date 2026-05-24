package top.fmutren.crh.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record PipeConnectionMessage(
        BlockPos pos,
        Direction face,
        boolean offHand,
        boolean shift
) {

}
