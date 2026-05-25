package top.fmutren.crh.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.fmutren.crh.api.CrhServices;

import static com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock.placementHelperId;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isShaftCasing;

@Mixin(
        value = ShaftBlock.class,
        remap = false
)
public class CreateShaftBlockMixin {

    @Inject(
            method = {
                    "use(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
                    "m_6227_(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"
            },
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            remap = false
    )
    private void crh$encaseAfterPlaceInShaft(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        ItemStack stack = player.getItemInHand(hand);
        crh$tryEncaseAfterPlaceInShaft(stack, state, level, pos, player, hand, hitResult, cir);
    }

    @Unique
    private void crh$tryEncaseAfterPlaceInShaft(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        var heldOffHandItem = player.getOffhandItem();

        if (stack.isEmpty() || !AllBlocks.SHAFT.isIn(stack) || !isShaftCasing(heldOffHandItem)) {
            return;
        }

        var helper = PlacementHelpers.get(placementHelperId);
        var offset = helper.getOffset(player, level, state, pos, hitResult);
        var vec3i = offset.getPos();
        var newPos = new BlockPos(vec3i.getX(), vec3i.getY(), vec3i.getZ());

        offset.placeInWorld(level, (BlockItem) stack.getItem(), player, hand, hitResult);

        var newState = level.getBlockState(newPos);
        CrhServices.create().tryEncase(newState, level, newPos, heldOffHandItem, player, hand, hitResult);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }

}
