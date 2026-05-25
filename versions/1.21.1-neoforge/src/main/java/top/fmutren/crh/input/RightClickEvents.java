package top.fmutren.crh.input;

import net.minecraft.world.InteractionResult;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class RightClickEvents {

    private RightClickEvents() {
    }

    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        boolean handled = RightClick.handleRightClickBlock(
                event.getLevel(),
                event.getPos(),
                event.getFace(),
                event.getHand(),
                event.getEntity(),
                event.getItemStack(),
                event.getHitVec()
        );

        if (handled) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(RightClick.ENCASE_MAPPING);
    }

}
