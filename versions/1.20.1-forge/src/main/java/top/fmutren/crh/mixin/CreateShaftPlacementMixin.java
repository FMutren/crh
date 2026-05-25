package top.fmutren.crh.mixin;

import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.fmutren.crh.interaction.StateSwitch;

@Mixin(
        value = ShaftBlock.class,
        remap = false
)
public abstract class CreateShaftPlacementMixin {

    @Inject(
            method = "getStateForPlacement",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
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
