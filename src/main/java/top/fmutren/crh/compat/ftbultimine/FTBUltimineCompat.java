package top.fmutren.crh.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;

public final class FTBUltimineCompat {

    private static boolean registered;

    private FTBUltimineCompat() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        RegisterRightClickHandlerEvent.REGISTER.register(registry ->
                registry.registerHandler(CrhUltimineRightClickHandler.INSTANCE)
        );
    }

}
