package top.fmutren.crh.compat.ftbultimine;

public final class FTBUltimineCompat {

    private static boolean registered;

    private FTBUltimineCompat() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        FTBRightClickHandle.register();
    }

}
