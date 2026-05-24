package top.fmutren.crh.input;

import net.minecraftforge.event.TickEvent;

public final class KeyDownEvents {

    private KeyDownEvents() {
    }

    public static void tick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        KeyDown.tickEnd(event.player);
    }

}
