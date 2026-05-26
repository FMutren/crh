package top.fmutren.crh.input;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import top.fmutren.crh.event.PlayerEventCreator;

public final class ClientEventRegister {

    private ClientEventRegister() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(RightClick::registerBindings);
        NeoForge.EVENT_BUS.addListener(PlayerEventCreator::tick);
        NeoForge.EVENT_BUS.addListener(RightClick::rightClickBlock);
    }

}
