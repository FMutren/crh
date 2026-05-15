package top.fmutren.crh.mixin;

import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static top.fmutren.crh.interaction.PlayerLookOnFace.getPlayerLookingFace;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isShaftCasing;

@Mixin(CogWheelBlock.class)
public class CreateCogWheelBlockMixin {

    @Inject(
            method = "setPlacedBy",
            at = @At("TAIL"),
            remap = false
    )
    private void crh$EncaseAfterPlace(
            Level worldIn,
            BlockPos pos,
            BlockState state,
            LivingEntity placer,
            ItemStack stack,
            CallbackInfo ci)
    {
        if (!(placer instanceof Player player)) return;

        ItemStack heldOffHandItem = player.getOffhandItem();
        Direction face = getPlayerLookingFace(player);

        BlockHitResult blockHit = centerHit(pos, face);

        if(isShaftCasing(heldOffHandItem) && state.getBlock() instanceof EncasableBlock encasableBlock)
        {
            encasableBlock.tryEncase(
                    state,
                    worldIn,
                    pos,
                    heldOffHandItem,
                    player,
                    InteractionHand.MAIN_HAND,
                    blockHit);
        }
    }
}