package top.fmutren.crh.mixin;

import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.fmutren.crh.interaction.ChainInteraction;

@Mixin(
        value = {
                FluidPipeBlock.class,
                ShaftBlock.class,
                BeltBlock.class,
                ChuteBlock.class,
                CogWheelBlock.class
        },
        remap = false
)
public abstract class CreateEncasingBlockMixin {

    @Inject(
            // "use" is the official-mapping name used in dev, "m_6227_" is the SRG
            // name present in the production jar. require = 0 tolerates the other
            // name being absent in the current environment.
            method = {"use", "m_6227_"},
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void crh$chainEncasingUse(
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult ray,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        InteractionResult result = ChainInteraction.tryHandleEncasing(player.getMainHandItem(), state, world, pos, player, hand, ray);
        if (result.consumesAction()) {
            cir.setReturnValue(result);
        }
    }

}
