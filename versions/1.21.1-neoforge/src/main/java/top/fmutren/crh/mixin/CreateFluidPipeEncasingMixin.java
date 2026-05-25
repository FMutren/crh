package top.fmutren.crh.mixin;

import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.fmutren.crh.api.ChainActionResult;
import top.fmutren.crh.interaction.ChainInteraction;

@Mixin(
        value = FluidPipeBlock.class,
        remap = false
)
public abstract class CreateFluidPipeEncasingMixin {

    @Inject(
            method = "useItemOn",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void crh$chainEncasingUse(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<ItemInteractionResult> cir
    ) {
        ChainActionResult result = ChainInteraction.tryHandleEncasing(stack, state, level, pos, player, hand, hitResult);
        if (result.consumesAction()) {
            cir.setReturnValue(crh$toItemInteractionResult(result));
        }
    }

    @Unique
    private ItemInteractionResult crh$toItemInteractionResult(ChainActionResult result) {
        return switch (result) {
            case PASS -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            case SUCCESS -> ItemInteractionResult.SUCCESS;
            case CONSUME -> ItemInteractionResult.CONSUME;
        };
    }

}
