package top.fmutren.crh.compat.copycats;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import dev.ftb.mods.ftbultimine.FTBUltiminePlayerData;
import dev.ftb.mods.ftbultimine.shape.ShapeContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

import static top.fmutren.crh.compat.ftbultimine.FTBRightClickHandle.playerPick;
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

        if(!(heldItem.getItem() instanceof BlockItem)) return 0;

        BlockHitResult referenceHit = playerPick(player);
        Direction interactionFace = face;
        Vec3 localHit = null;
        if (referenceHit != null && positions.contains(referenceHit.getBlockPos())) {
            interactionFace = referenceHit.getDirection();
            localHit = referenceHit.getLocation()
                    .subtract(Vec3.atLowerCornerOf(referenceHit.getBlockPos()));
        }

        int count = 0;

        for(BlockPos pos : positions) {
            BlockState targetState = level.getBlockState(pos);
            if(!(targetState.getBlock() instanceof ICopycatBlock)) continue;

            BlockHitResult hit = localHit != null
                    ? new BlockHitResult(Vec3.atLowerCornerOf(pos).add(localHit), interactionFace, pos, false)
                    : centerHit(pos, interactionFace);

            InteractionResult result = targetState.use(level, player, hand, hit);
            if (result.consumesAction()) count++;
        }

        return count;
    }
}
