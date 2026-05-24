package top.fmutren.crh.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;

public final class FTBRightClickHandle {

    private FTBRightClickHandle() {
    }

    public static void register() {
        RegisterRightClickHandlerEvent.REGISTER.register(registry ->
                registry.registerHandler((context, hand, positions) ->
                        FTBUltimineCommonHandler.handle(
                                context.player(),
                                hand,
                                positions,
                                context.origPos()
                        )
                )
        );
    }

}
