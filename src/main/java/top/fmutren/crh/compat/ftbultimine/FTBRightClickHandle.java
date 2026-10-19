package top.fmutren.crh.compat.ftbultimine;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import top.fmutren.crh.interaction.ChainDelegation;

import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isCasing;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isWrench;


public class FTBRightClickHandle {
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

            int count = 0;

            if(isWrench(heldItem)){
                final int[] processed = {0};
                WrenchChainGuard.run(player, () -> {
                    BlockHitResult referenceHit = playerPick(player);
                    Direction interactionFace = context.face();
                    Vec3 localHit = null;
                    if (referenceHit != null && referenceHit.getBlockPos().equals(context.origPos())) {
                        interactionFace = referenceHit.getDirection();
                        localHit = referenceHit.getLocation()
                                .subtract(Vec3.atLowerCornerOf(referenceHit.getBlockPos()));
                    }
                    for (BlockPos pos : positions) {

                        BlockHitResult hit = localHit != null
                                ? new BlockHitResult(Vec3.atLowerCornerOf(pos).add(localHit), interactionFace, pos, false)
                                : centerHit(pos, interactionFace);

                        BlockState state = level.getBlockState(pos);
                        if (!(state.getBlock() instanceof IWrenchable wrenchable)) {
                            continue;
                        }
                        UseOnContext useOnContext = new UseOnContext(level,
                                player,
                                hand,
                                heldItem,
                                hit);
                        InteractionResult result = player.isShiftKeyDown()
                                ? wrenchable.onSneakWrenched(state, useOnContext)
                                : wrenchable.onWrenched(state, useOnContext);
                        if (result.consumesAction()) {
                            processed[0]++;
                        }
                    }
                });
                count = processed[0];
            }
            else if(isCasing(heldItem)){
                if(player.isShiftKeyDown()) return 0;

                BlockHitResult referenceHit = playerPick(player);
                Direction interactionFace = context.face();
                Vec3 localHit = null;
                if (referenceHit != null && referenceHit.getBlockPos().equals(context.origPos())) {
                    interactionFace = referenceHit.getDirection();
                    localHit = referenceHit.getLocation()
                            .subtract(Vec3.atLowerCornerOf(referenceHit.getBlockPos()));
                }

                for (BlockPos pos : positions) {

                    BlockHitResult hit = localHit != null
                            ? new BlockHitResult(Vec3.atLowerCornerOf(pos).add(localHit), interactionFace, pos, false)
                            : centerHit(pos, interactionFace);


                    BlockState state = level.getBlockState(pos);
                    ChainDelegation.enter();
                    try {
                        state.useItemOn(heldItem, level, player, hand, hit);
                    } finally {
                        ChainDelegation.exit();
                    }
                }
                count++;
            }
            return count;
        }));
    }

    public static BlockHitResult playerPick(Player player) {
        double reach = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        if (player.isCreative()) reach += 0.5D;
        HitResult hit = player.pick(reach, 1.0F, false);
        return hit instanceof BlockHitResult blockHit ? blockHit : null;
    }
}
