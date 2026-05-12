package top.fmutren.crh.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
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
import top.fmutren.crh.interaction.ShaftSwitch;

import static com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock.placementHelperId;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isShaftCasing;


@Mixin(ShaftBlock.class)
public class CreateShaftBlockMixin {

    @Inject(
            method = "getStateForPlacement",
            at = @At("RETURN"),
            cancellable = true
    )
    private void crh$EncaseAfterPlace(
            BlockPlaceContext context,
            CallbackInfoReturnable<BlockState> cir
    )
    {
        Player player = context.getPlayer();
        if (player == null) return;

        ItemStack heldOffHandItem = player.getOffhandItem();

        BlockState state = ShaftSwitch.ShaftSwitchToBlockState(heldOffHandItem, cir.getReturnValue());

        cir.setReturnValue(state);
    }

    @Inject(
            method = "useItemOn",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void crh$EncaseAfterPlaceInShaft(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<ItemInteractionResult> cir
    )
    {
        ItemStack heldOffHandItem = player.getOffhandItem();

        if(!AllBlocks.SHAFT.isIn(stack) || stack.isEmpty()) return;
        if(!isShaftCasing(heldOffHandItem)) return;

        IPlacementHelper helper = PlacementHelpers.get(placementHelperId);
        Vec3i vec3i = helper.getOffset(player, level, state, pos, hitResult).getPos();
        BlockPos newPos = new BlockPos(vec3i.getX(), vec3i.getY(), vec3i.getZ());

        helper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem) stack.getItem(), player, hand, hitResult);

        BlockState newState = level.getBlockState(newPos);

        if(!(state.getBlock() instanceof EncasableBlock encasableBlock)) return;
        encasableBlock.tryEncase(newState, level, newPos, heldOffHandItem, player, hand, hitResult);

        cir.setReturnValue(ItemInteractionResult.SUCCESS);
    }
}
