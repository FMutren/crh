package top.fmutren.crh.mixinhook;

import com.simibubi.create.AllBlocks;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import top.fmutren.crh.api.ChainActionResult;
import top.fmutren.crh.api.CrhServices;
import top.fmutren.crh.interaction.ChainInteraction;
import top.fmutren.crh.interaction.StateSwitch;

import static com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock.placementHelperId;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isShaftCasing;

public final class CreateEncasingHooks {

    private CreateEncasingHooks() {
    }

    public static ChainActionResult tryHandleEncasing(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        return ChainInteraction.tryHandleEncasing(stack, state, level, pos, player, hand, hitResult);
    }

    public static boolean tryEncaseAfterPlaceInShaft(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (player == null) {
            return false;
        }

        var heldOffHandItem = player.getOffhandItem();
        if (stack == null || stack.isEmpty() || !AllBlocks.SHAFT.isIn(stack) || !isShaftCasing(heldOffHandItem)) {
            return false;
        }

        var helper = PlacementHelpers.get(placementHelperId);
        var offset = helper.getOffset(player, level, state, pos, hitResult);
        var vec3i = offset.getPos();
        var newPos = new BlockPos(vec3i.getX(), vec3i.getY(), vec3i.getZ());

        offset.placeInWorld(level, (BlockItem) stack.getItem(), player, hand, hitResult);

        var newState = level.getBlockState(newPos);
        CrhServices.create().tryEncase(newState, level, newPos, heldOffHandItem, player, hand, hitResult);
        return true;
    }

    public static BlockState modifyShaftPlacementState(BlockPlaceContext context, BlockState original) {
        if (context == null) {
            return original;
        }

        var player = context.getPlayer();
        if (player == null) {
            return original;
        }

        return StateSwitch.shaftSwitchToBlockState(player.getOffhandItem(), original);
    }

}
