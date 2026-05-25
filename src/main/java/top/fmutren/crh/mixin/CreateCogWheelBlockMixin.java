package top.fmutren.crh.mixin;

import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static top.fmutren.crh.interaction.PlayerLookOnFace.getPlayerLookingFace;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isShaftCasing;

@Mixin(value = CogWheelBlock.class, remap = false)
public class CreateCogWheelBlockMixin {

    @Inject(
            method = {
                    "setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V",
                    "m_6402_(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"
            },
            at = @At("TAIL"),
            require = 0,
            remap = false
    )
    private void crh$encaseAfterPlace(
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
        if (!isShaftCasing(heldOffHandItem) || !(state.getBlock() instanceof EncasableBlock encasableBlock)) {
            return;
        }

        var face = getPlayerLookingFace(player);
        var blockHit = centerHit(pos, face);
        encasableBlock.tryEncase(
                state,
                level,
                pos,
                heldOffHandItem,
                player,
                InteractionHand.MAIN_HAND,
                blockHit
        );
    }

}
