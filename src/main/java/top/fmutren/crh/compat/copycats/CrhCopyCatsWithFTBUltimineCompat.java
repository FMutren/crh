package top.fmutren.crh.compat.copycats;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static top.fmutren.crh.compat.ftbultimine.FTBRightClickHandle.playerPick;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;

public class CrhCopyCatsWithFTBUltimineCompat {
    public static void FTBRightClickEventHandler() {
        RegisterRightClickHandlerEvent.REGISTER.register(registry ->
                registry.registerHandler((context,
                                          hand,
                                          positions) ->
                {
                    Player player = context.player();
                    if(player.isSpectator() || !player.mayBuild()) return 0;
                    Level level = player.level();
                    ItemStack heldItem = player.getItemInHand(hand);

                    if(!(heldItem.getItem() instanceof BlockItem)) return 0;

                    BlockHitResult referenceHit = playerPick(player);
                    Direction interactionFace = context.face();
                    Vec3 localHit = null;
                    if (referenceHit != null && referenceHit.getBlockPos().equals(context.origPos())) {
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

                        ItemInteractionResult result = targetState.useItemOn(heldItem, level, player, hand, hit);
                        if (result.consumesAction()) count++;
                    }

                    return count;
                }));
    }
}
