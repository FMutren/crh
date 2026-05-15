package top.fmutren.crh.input;



import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;


public final class ClientEventRegister {

    private ClientEventRegister() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(RightClick::registerBindings);
        MinecraftForge.EVENT_BUS.addListener(KeyDown::tick);
        MinecraftForge.EVENT_BUS.addListener(RightClick::rightClickBlock);
    }
}
