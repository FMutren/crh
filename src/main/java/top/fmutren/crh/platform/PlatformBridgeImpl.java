package top.fmutren.crh.platform;

//? if forge {
/*import net.minecraftforge.fml.ModList;
 *///?} else {
import net.neoforged.fml.ModList;
//?}

import top.fmutren.crh.Config;
import top.fmutren.crh.api.PlatformBridge;

public final class PlatformBridgeImpl implements PlatformBridge {

    @Override
    public int maxPipeBlocks() {
        return Config.maxPipeBlocks();
    }

    @Override
    public int maxShaftBlocks() {
        return Config.maxShaftBlocks();
    }

    @Override
    public int maxBeltBlocks() {
        return Config.maxBeltBlocks();
    }

    @Override
    public double maxEmptyHandPipeReachSqr() {
        return Config.maxEmptyHandPipeReachSqr();
    }

    @Override
    public boolean enableView() {
        return Config.enableView() && !isModLoaded("ftbultimine");
    }

    @Override
    public boolean enableEmptyHandModifyPipe() {
        return Config.enableEmptyHandModifyPipe();
    }

    @Override
    public boolean builtinChainEnabled() {
        boolean ftbUltimineCompatActive =
                Config.compatFtbUltimine() && isModLoaded("ftbultimine");

        return !(ftbUltimineCompatActive
                && Config.disableBuiltinChainWhenFtbUltimineCompatEnabled());
    }

    @Override
    public boolean isModLoaded(String modId) {
        return modId != null && ModList.get().isLoaded(modId);
    }

}
