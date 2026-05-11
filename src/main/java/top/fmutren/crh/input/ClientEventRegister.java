package top.fmutren.crh.input;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import top.fmutren.crh.Crh;

@Mod(value = Crh.MODID, dist = Dist.CLIENT)
public class ClientEventRegister {

    public static final String MODID = "crh";

    public ClientEventRegister(IEventBus bus) {
        bus.addListener(RightClick::registerBindings);
        NeoForge.EVENT_BUS.addListener(KeyDown::TickEvent);
        NeoForge.EVENT_BUS.addListener(RightClick::RightClickEvent);
    }

}
