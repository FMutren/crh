package top.fmutren.crh.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.fluids.pipes.AxisPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fmutren.crh.platform.ResourceLocations;

import static top.fmutren.crh.interaction.PlayerLookOnFace.fluidPipeFace;
import static top.fmutren.crh.interaction.PlayerLookOnFace.getPlayerLookingFace;
import static top.fmutren.crh.interaction.StateSwitch.isCreateWrench;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;

@Mixin(FluidPipeBlock.class)
public abstract class CreateFluidPipeMixin {

    @Inject(
            method = "setPlacedBy",
            at = @At("TAIL")
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
        var face = getPlayerLookingFace(player);
        var blockHit = centerHit(pos, face);

        if (!(state.getBlock() instanceof EncasableBlock encasableBlock)) {
            return;
        }

        if (AllBlocks.COPPER_CASING.isIn(heldOffHandItem)) {
            encasableBlock.tryEncase(
                    state,
                    level,
                    pos,
                    heldOffHandItem,
                    player,
                    InteractionHand.MAIN_HAND,
                    blockHit
            );
            return;
        }

        if (isCreateWrench(heldOffHandItem)) {
            BlockState newState = BuiltInRegistries.BLOCK.get(ResourceLocations.parse("create:glass_fluid_pipe"))
                    .defaultBlockState()
                    .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED))
                    .setValue(AxisPipeBlock.AXIS, fluidPipeFace(player, state));
            level.setBlockAndUpdate(pos, newState);
        }
    }

}
