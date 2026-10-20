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
            // "useOn" is the official-mapping name used in dev, "m_6225_" is the SRG
            // name present in the production jar. require = 0 tolerates the other
            // name being absent in the current environment.
            method = {"useOn", "m_6225_"},
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void crh$chainWrenchUse(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult result = ChainInteraction.tryHandleWrench(context);
        if (result.consumesAction()) {
            cir.setReturnValue(result);
        }
    }

}
