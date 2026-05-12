package top.fmutren.crh.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidPipeBlock.class)
public abstract class CreateFluidPipeMixin {

    @Inject(
            method = "setPlacedBy",
            at = @At("TAIL")
    )
    private void crh$EncaseAfterPlace(
            Level pLevel,
            BlockPos pPos,
            BlockState pState,
            LivingEntity pPlacer,
            ItemStack pStack,
            CallbackInfo ci
    )
    {
        if (!(pPlacer instanceof Player player)) return;

        ItemStack heldOffHandItem = player.getOffhandItem();

        HitResult hit = Minecraft.getInstance().hitResult;
        if (hit.getType() != HitResult.Type.BLOCK) return;
        BlockHitResult blockHit = (BlockHitResult) hit;

        if(!(pState.getBlock() instanceof EncasableBlock encasableBlock)) return;

        if(AllBlocks.COPPER_CASING.isIn(heldOffHandItem))
        {
            encasableBlock.tryEncase(
                    pState,
                    pLevel,
                    pPos,
                    heldOffHandItem,
                    player,
                    InteractionHand.MAIN_HAND,
                    blockHit);
        }
    }
}
