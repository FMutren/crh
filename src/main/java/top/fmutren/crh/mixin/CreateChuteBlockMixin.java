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

@Mixin(value = AbstractChuteBlock.class, remap = false)
public class CreateChuteBlockMixin {

    @Inject(
            method = {
                    "setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V",
                    "m_6402_(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"
            },
            at = @At("TAIL"),
            require = 0,
            remap = false
    )
    private void crh$afterPlace(
            Level level,
            BlockPos pos,
            BlockState state,
            LivingEntity placer,
            ItemStack stack,
            CallbackInfo ci
    ) {
        if (!(placer instanceof Player player)) {
            return;
        }

        var heldOffHandItem = player.getOffhandItem();
        AbstractChuteBlock block = (AbstractChuteBlock) state.getBlock();
        var facing = block.getFacing(state);

        if (AllBlocks.INDUSTRIAL_IRON_BLOCK.isIn(heldOffHandItem)) {
            BlockState newState = state.setValue(ChuteBlock.SHAPE, ChuteBlock.Shape.ENCASED);
            level.setBlockAndUpdate(pos, newState);
            return;
        }

        if (AllItems.WRENCH.isIn(heldOffHandItem) && facing == Direction.DOWN) {
            BlockState newState = state.setValue(ChuteBlock.SHAPE, ChuteBlock.Shape.WINDOW);
            level.setBlockAndUpdate(pos, newState);
        }
    }

}
