package top.fmutren.crh.input;

import net.minecraftforge.common.MinecraftForge;

public final class ClientEventRegister {

    private ClientEventRegister() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(KeyDownEvents::tick);
        MinecraftForge.EVENT_BUS.addListener(RightClickEvents::rightClickBlock);
    }

}
