package top.fmutren.crh.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.fluids.pipes.AxisPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static top.fmutren.crh.interaction.PlayerLookOnFace.FluidPipeFace;
import static top.fmutren.crh.interaction.PlayerLookOnFace.getPlayerLookingFace;
import static top.fmutren.crh.interaction.StateSwitch.isCreateWrench;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;


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
    ) {
        if (!(pPlacer instanceof Player player)) return;

        ItemStack heldOffHandItem = player.getOffhandItem();
        Direction face = getPlayerLookingFace(player);
        BlockHitResult blockHit = centerHit(pPos, face);

        if (!(pState.getBlock() instanceof EncasableBlock encasableBlock)) return;

        if (AllBlocks.COPPER_CASING.isIn(heldOffHandItem)) {
            encasableBlock.tryEncase(
                    pState,
                    pLevel,
                    pPos,
                    heldOffHandItem,
                    player,
                    InteractionHand.MAIN_HAND,
                    blockHit
            );
        } else if (isCreateWrench(heldOffHandItem)) {
            BlockState newState = BuiltInRegistries.BLOCK.get(ResourceLocation
                    .parse("create:glass_fluid_pipe"))
                    .defaultBlockState()
                    .setValue(BlockStateProperties.WATERLOGGED, pState.getValue(BlockStateProperties.WATERLOGGED))
                    .setValue(AxisPipeBlock.AXIS, FluidPipeFace(player, pState));
            pLevel.setBlockAndUpdate(pPos, newState);
        }
    }
}
