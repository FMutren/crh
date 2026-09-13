package top.fmutren.crh.compat.ftbultimine;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import dev.ftb.mods.ftbultimine.FTBUltiminePlayerData;
import dev.ftb.mods.ftbultimine.shape.ShapeContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.Collection;

import static top.fmutren.crh.interaction.StateSwitch.iterationTypeForItem;
import static top.fmutren.crh.interaction.TryToEncase.tryToEncaseBelt;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;


public class FTBRightClickHandle {
    public static int FTBRightClickEventHandler(ShapeContext context,
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

        switch (iterationTypeForItem(heldItem)) {
            case WRENCH -> {
                final int[] processed = {0};
                WrenchChainGuard.run(player, () -> {
                    BlockHitResult referenceHit = playerPick(player);
                    Direction interactionFace = face;
                    Vec3 localHit = null;
                    if (referenceHit != null && positions.contains(referenceHit.getBlockPos())) {
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

                        UseOnContext useOnContext = new UseOnContext(level, player, hand, heldItem, hit);
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
            case COMMON_CASING, PIPE_CASING, CHUTE_CASING -> {
                if(player.isShiftKeyDown()) return 0;

                BlockHitResult referenceHit = playerPick(player);
                Direction interactionFace = face;
                Vec3 localHit = null;
                if (referenceHit != null && positions.contains(referenceHit.getBlockPos())) {
                    interactionFace = referenceHit.getDirection();
                    localHit = referenceHit.getLocation()
                            .subtract(Vec3.atLowerCornerOf(referenceHit.getBlockPos()));
                }

                int processed = 0;
                for (BlockPos pos : positions) {
                    BlockHitResult hit = localHit != null
                            ? new BlockHitResult(Vec3.atLowerCornerOf(pos).add(localHit), interactionFace, pos, false)
                            : centerHit(pos, interactionFace);

                    BlockState state = level.getBlockState(pos);
                    if (tryToEncaseBelt(heldItem, pos, level)) {
                        processed++;
                        continue;
                    }
                    if (state.use(level, player, hand, hit).consumesAction()) {
                        processed++;
                    }
                }
                count = processed;
            }
        }
        return count;
    }

    public static BlockHitResult playerPick(Player player) {
        double reach = player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
        if (player.isCreative()) reach += 0.5D;
        HitResult hit = player.pick(reach, 1.0F, false);
        return hit instanceof BlockHitResult blockHit ? blockHit : null;
    }
}
