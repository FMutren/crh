package top.fmutren.crh.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.fmutren.crh.interaction.StateSwitch;

import static com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock.placementHelperId;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isShaftCasing;

@Mixin(ShaftBlock.class)
public class CreateShaftBlockMixin {

    @Inject(
            method = "useItemOn",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void crh$encaseAfterPlaceInShaft(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<ItemInteractionResult> cir
    ) {
        var heldOffHandItem = player.getOffhandItem();

        if (stack.isEmpty() || !AllBlocks.SHAFT.isIn(stack) || !isShaftCasing(heldOffHandItem)) {
            return;
        }

        IPlacementHelper helper = PlacementHelpers.get(placementHelperId);
        var offset = helper.getOffset(player, level, state, pos, hitResult);
        var vec3i = offset.getPos();
        BlockPos newPos = new BlockPos(vec3i.getX(), vec3i.getY(), vec3i.getZ());

        offset.placeInWorld(level, (BlockItem) stack.getItem(), player, hand, hitResult);

        if (!(state.getBlock() instanceof EncasableBlock encasableBlock)) {
            return;
        }

        var newState = level.getBlockState(newPos);
        encasableBlock.tryEncase(newState, level, newPos, heldOffHandItem, player, hand, hitResult);
        cir.setReturnValue(ItemInteractionResult.SUCCESS);
    }

    @Inject(
            method = "getStateForPlacement",
            at = @At("RETURN"),
            cancellable = true
    )
    private void crh$encaseAfterPlace(
            BlockPlaceContext context,
            CallbackInfoReturnable<BlockState> cir
    ) {
        var player = context.getPlayer();
        if (player == null) {
            return;
        }

        var heldOffHandItem = player.getOffhandItem();
        var state = StateSwitch.shaftSwitchToBlockState(heldOffHandItem, cir.getReturnValue());
        cir.setReturnValue(state);
    }

}
