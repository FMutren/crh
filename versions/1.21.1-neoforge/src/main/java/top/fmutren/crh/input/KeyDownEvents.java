package top.fmutren.crh.input;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class KeyDownEvents {

    private KeyDownEvents() {
    }

    public static void tick(PlayerTickEvent.Post event) {
        KeyDown.tickEnd(event.getEntity());
    }

}
