package top.fmutren.crh.compat.copycats;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import dev.ftb.mods.ftbultimine.FTBUltiminePlayerData;
import dev.ftb.mods.ftbultimine.shape.ShapeContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;

import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;

public class CopycatsWithFTBUltimineCompat {
    public static int CopycatsWithFTBUltimineRightClickHandler(ShapeContext context,
                                                InteractionHand hand,
                                                FTBUltiminePlayerData data,
                                                Direction face
    ) {
        Player player = context.player();
        if(player.isSpectator() || !player.mayBuild()) return 0;
        Level level = player.level();
        ItemStack heldItem = player.getItemInHand(hand);
        Collection<BlockPos> positions = data.cachedPositions();

        int count = 0;

        if(heldItem.getItem() instanceof BlockItem) {
            for(BlockPos pos : positions) {
                BlockState targetState = level.getBlockState(pos);
                if(targetState.getBlock() instanceof ICopycatBlock icb) {
                    icb.use(targetState, level, pos, player, InteractionHand.MAIN_HAND, centerHit(pos, face));
                    count++;
                }

            }
        }

        return count;
    }
}
