package top.fmutren.crh.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;

public final class FtbRightClickHandle {

    private FtbRightClickHandle() {
    }

    public static void register() {
        RegisterRightClickHandlerEvent.REGISTER.register(registry ->
                registry.registerHandler((context, hand, positions) ->
                        FtbUltimineCommonHandler.handle(
                                context.player(),
                                hand,
                                positions,
                                context.origPos()
                        )
                )
        );
    }

}
