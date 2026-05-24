package top.fmutren.crh.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface CreateBridge {

    ChainActionResult tryEncase(
            BlockState state,
            Level level,
            BlockPos pos,
            ItemStack stack,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    );

    Item fluidPipeItem();

}
