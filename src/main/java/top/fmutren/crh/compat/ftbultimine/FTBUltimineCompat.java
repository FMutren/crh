package top.fmutren.crh.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.restriction.RegisterRestrictionHandlerEvent;

import static top.fmutren.crh.compat.ftbultimine.FTBRightClickHandle.FTBRightClickEventHandler;

public final class FTBUltimineCompat {

    private FTBUltimineCompat() {}

    public static void register() {
        RegisterRestrictionHandlerEvent.REGISTER.register(registry ->
                registry.register(player -> !WrenchChainGuard.isActive(player)));
        FTBRightClickEventHandler();
    }

}
