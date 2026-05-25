package top.fmutren.crh.api;

public interface PlatformBridge {

    int maxPipeBlocks();

    int maxShaftBlocks();

    int maxBeltBlocks();

    double maxEmptyHandPipeReachSqr();

    boolean enableView();

    boolean enableEmptyHandModifyPipe();

    boolean builtinChainEnabled();

}
