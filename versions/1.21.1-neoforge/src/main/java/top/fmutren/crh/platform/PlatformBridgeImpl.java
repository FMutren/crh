package top.fmutren.crh.platform;

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
        return Config.enableView();
    }

    @Override
    public boolean enableEmptyHandModifyPipe() {
        return Config.enableEmptyHandModifyPipe();
    }

    @Override
    public boolean builtinChainEnabled() {
        return Config.builtinChainAllowed();
    }

}
