package top.fmutren.crh.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;
import net.minecraft.core.BlockPos;

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
                                originPos(positions, context.player().blockPosition())
                        )
                )
        );
    }

    private static BlockPos originPos(
            Iterable<BlockPos> positions,
            BlockPos fallback
    ) {
        if (positions == null) {
            return fallback;
        }

        var iterator = positions.iterator();
        return iterator.hasNext() ? iterator.next() : fallback;
    }

}
