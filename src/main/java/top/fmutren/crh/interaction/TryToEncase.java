package top.fmutren.crh.interaction;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isCasing;

public class TryToEncase {

    private TryToEncase(){}

    public static InteractionResult tryToEncase(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack heldItem, Direction face) {
        if(!(state.getBlock() instanceof EncasableBlock) && !AllBlocks.BELT.has(state)) return InteractionResult.PASS;
        if(!(isCasing(heldItem))) return InteractionResult.PASS;

        ChainDelegation.enter();
        try {
            return state.use(
                    level,
                    player,
                    hand,
                    centerHit(pos, face)
            );
        } finally {
            ChainDelegation.exit();
        }
    }
}
