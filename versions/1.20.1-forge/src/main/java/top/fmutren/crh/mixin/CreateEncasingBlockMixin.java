package top.fmutren.crh.mixin;

import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.fmutren.crh.api.ChainActionResult;
import top.fmutren.crh.interaction.ChainInteraction;

@Mixin(
        value = {
                FluidPipeBlock.class,
                ShaftBlock.class,
                BeltBlock.class
        },
        remap = false
)
public abstract class CreateEncasingBlockMixin {

    @Inject(
            method = "use",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void crh$chainEncasingUse(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        ItemStack stack = player.getItemInHand(hand);
        ChainActionResult result = ChainInteraction.tryHandleEncasing(stack, state, level, pos, player, hand, hitResult);
        if (result.consumesAction()) {
            cir.setReturnValue(crh$toInteractionResult(result));
        }
    }

    private InteractionResult crh$toInteractionResult(ChainActionResult result) {
        return switch (result) {
            case PASS -> InteractionResult.PASS;
            case SUCCESS -> InteractionResult.SUCCESS;
            case CONSUME -> InteractionResult.CONSUME;
        };
    }

}
