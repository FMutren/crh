package top.fmutren.crh.interaction;

import net.minecraft.core.BlockPos;

import java.util.List;

public record ChainSelection(
        List<BlockPos> positions,
        boolean truncated
) {

    public ChainSelection {
        positions = List.copyOf(positions);
    }

    public static ChainSelection empty() {
        return new ChainSelection(List.of(), false);
    }

    public boolean isEmpty() {
        return positions.isEmpty();
    }

}
