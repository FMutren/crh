package top.fmutren.crh.platform;

import com.simibubi.create.AllBlocks;
import net.minecraft.world.item.Item;

public final class CreateBridgeImpl extends AbstractCreateBridge {

    @Override
    protected Item fluidPipeItemCompat() {
        return AllBlocks.FLUID_PIPE.asItem();
    }

}
