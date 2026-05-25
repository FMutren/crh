package top.fmutren.crh.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
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

    BeltCasingKind beltCasingKind(ItemStack stack);

    boolean isBelt(BlockState state);

    boolean applyBeltCasing(
            Level level,
            BlockPos pos,
            BlockState state,
            BeltCasingKind kind,
            Player player
    );

    boolean isManualPipe(BlockState state);

    boolean isPlainFluidPipe(BlockState state);

    void beforePipeStateChange(Level level, BlockPos pos);

    void afterPipeStateChange(Level level, BlockPos pos);

    ChainActionResult applyWrench(
            BlockState state,
            UseOnContext context,
            boolean sneaking
    );

}
