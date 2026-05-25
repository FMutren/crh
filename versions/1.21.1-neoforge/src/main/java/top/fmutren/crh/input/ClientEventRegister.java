package top.fmutren.crh.input;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public final class ClientEventRegister {

    private ClientEventRegister() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(RightClickEvents::registerBindings);
        NeoForge.EVENT_BUS.addListener(KeyDownEvents::tick);
    }

}
