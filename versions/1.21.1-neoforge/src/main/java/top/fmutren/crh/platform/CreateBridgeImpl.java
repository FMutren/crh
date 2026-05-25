package top.fmutren.crh.platform;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import top.fmutren.crh.api.ChainActionResult;
import top.fmutren.crh.api.CreateBridge;

public final class CreateBridgeImpl implements CreateBridge {

    @Override
    public ChainActionResult tryEncase(
            BlockState state,
            Level level,
            BlockPos pos,
            ItemStack stack,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (!(state.getBlock() instanceof EncasableBlock encasableBlock)) {
            return ChainActionResult.PASS;
        }

        var result = encasableBlock.tryEncase(state, level, pos, stack, player, hand, hitResult);
        return result.consumesAction() ? ChainActionResult.SUCCESS : ChainActionResult.PASS;
    }

    @Override
    public Item fluidPipeItem() {
        return AllBlocks.FLUID_PIPE.asItem();
    }

}
