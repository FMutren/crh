package top.fmutren.crh.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractChuteBlock.class)
public class CreateChuteBlockMixin {

    @Inject(
            method = "setPlacedBy",
            at = @At("TAIL")
    )
    private void crh$AfterPlace(
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
        AbstractChuteBlock block = (AbstractChuteBlock) pState.getBlock();
        Direction facing = block.getFacing(pState);

        if (AllBlocks.INDUSTRIAL_IRON_BLOCK.isIn(heldOffHandItem)) {
            BlockState newState = pState.setValue(ChuteBlock.SHAPE, ChuteBlock.Shape.ENCASED);
            pLevel.setBlockAndUpdate(pPos, newState);
        } else if (AllItems.WRENCH.isIn(heldOffHandItem) && facing == Direction.DOWN) {
            BlockState newState = pState.setValue(ChuteBlock.SHAPE, ChuteBlock.Shape.WINDOW);
            pLevel.setBlockAndUpdate(pPos, newState);
        }
    }
}
