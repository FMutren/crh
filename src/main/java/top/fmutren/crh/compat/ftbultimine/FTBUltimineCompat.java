package top.fmutren.crh.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.FTBUltimine;

public final class FTBUltimineCompat {

    private FTBUltimineCompat() {
    }

    public static void register() {
        FTBUltimine.setPermissionOverride(player -> !WrenchChainGuard.isActive(player));
    }
}
