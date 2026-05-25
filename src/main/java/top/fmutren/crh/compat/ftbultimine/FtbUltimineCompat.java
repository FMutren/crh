package top.fmutren.crh.compat.ftbultimine;

public final class FtbUltimineCompat {

    private static boolean registered;

    private FtbUltimineCompat() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        FtbRightClickHandle.register();
    }

}
