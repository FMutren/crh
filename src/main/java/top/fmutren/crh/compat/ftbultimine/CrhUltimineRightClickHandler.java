package top.fmutren.crh.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.rightclick.RightClickHandler;
import dev.ftb.mods.ftbultimine.api.shape.ShapeContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;

import java.util.Collection;

public enum CrhUltimineRightClickHandler implements RightClickHandler {

    INSTANCE;

    @Override
    public int handleRightClickBlock(
            ShapeContext shapeContext,
            InteractionHand hand,
            Collection<BlockPos> positions
    ) {
        return 0;
    }

}
