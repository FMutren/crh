package top.fmutren.crh.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;
import dev.ftb.mods.ftbultimine.api.shape.RegisterShapeEvent;

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

        RegisterShapeEvent.REGISTER.register(registry ->
                registry.register(CrhChainShape.INSTANCE)
        );
    }

}
