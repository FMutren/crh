package top.fmutren.crh.compat.ftbultimine;

import static top.fmutren.crh.compat.ftbultimine.FTBRightClickHandle.FTBRightClickEventHandler;

public final class FTBUltimineCompat {

    private static boolean registered;

    private FTBUltimineCompat() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        FTBRightClickEventHandler();
    }

}
