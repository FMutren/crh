package top.fmutren.crh.mixin;

import com.simibubi.create.content.equipment.wrench.WrenchItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.fmutren.crh.interaction.ChainInteraction;

@Mixin(
        value = WrenchItem.class,
        remap = false
)
public abstract class CreateWrenchItemMixin {

    @Inject(
            method = "useOn",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void crh$chainWrenchUse(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult result = ChainInteraction.tryHandleWrench(context);
        if (result.consumesAction()) {
            cir.setReturnValue(result);
        }
    }

}
