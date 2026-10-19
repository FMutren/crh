package top.fmutren.crh.interaction;

/**
 * 标记“当前正在由 CRH 主动委托执行 state.useItemOn(...)”。
 * <p>
 * 委托调用会再次进入被 CRH mixin 注入的 Block#useItemOn，如果不加标记，
 * 会形成 tryHandleEncasing → applyEncasing → useItemOn → tryHandleEncasing 的无限递归。
 */
public final class ChainDelegation {

    private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);

    private ChainDelegation() {
    }

    public static boolean isActive() {
        return DEPTH.get() > 0;
    }

    public static void enter() {
        DEPTH.set(DEPTH.get() + 1);
    }

    public static void exit() {
        int depth = DEPTH.get() - 1;
        if (depth <= 0) {
            DEPTH.remove();
        } else {
            DEPTH.set(depth);
        }
    }
}
