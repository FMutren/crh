package top.fmutren.crh.server;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import top.fmutren.crh.mixinhook.CreatePlacementHooks;

public final class ServerEventHandlersNeoForge {

    private ServerEventHandlersNeoForge() {
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerEventHandlers.onPlayerLoggedOut(event.getEntity());
    }

    public static void onEntityPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof LivingEntity placer) {
            CreatePlacementHooks.afterBlockPlaced(event.getLevel(), event.getPos(), event.getPlacedBlock(), placer);
        }
    }

}
