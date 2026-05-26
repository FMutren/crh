package top.fmutren.crh.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;

import static top.fmutren.crh.Config.enableAutoInteraction;
import static top.fmutren.crh.interaction.AfterPlaced.tryAutoEncase;
import static top.fmutren.crh.interaction.AfterPlaced.tryAutoOpenWindow;

public class BlockEventCreator {
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if(!enableAutoInteraction()) return;
        if(!(event.getLevel() instanceof Level level)) return;
        if (level.isClientSide) return;
        if (!(event.getEntity() instanceof Player player)) return;

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        tryAutoEncase(state, level, pos, player);
        tryAutoOpenWindow(state, level, pos, player);
    }
}
