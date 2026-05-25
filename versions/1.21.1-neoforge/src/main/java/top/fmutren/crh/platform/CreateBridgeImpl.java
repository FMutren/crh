package top.fmutren.crh.platform;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import top.fmutren.crh.api.BeltCasingKind;
import top.fmutren.crh.api.ChainActionResult;
import top.fmutren.crh.api.CreateBridge;

public final class CreateBridgeImpl implements CreateBridge {

    @Override
    public ChainActionResult tryEncase(
            BlockState state,
            Level level,
            BlockPos pos,
            ItemStack stack,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (!(state.getBlock() instanceof EncasableBlock encasableBlock)) {
            return ChainActionResult.PASS;
        }

        var result = encasableBlock.tryEncase(state, level, pos, stack, player, hand, hitResult);
        return result.consumesAction() ? ChainActionResult.SUCCESS : ChainActionResult.PASS;
    }

    @Override
    public Item fluidPipeItem() {
        return AllBlocks.FLUID_PIPE.asItem();
    }

    @Override
    public BeltCasingKind beltCasingKind(ItemStack stack) {
        if (AllBlocks.ANDESITE_CASING.isIn(stack)) {
            return BeltCasingKind.ANDESITE;
        }
        if (AllBlocks.BRASS_CASING.isIn(stack)) {
            return BeltCasingKind.BRASS;
        }
        if (AllBlocks.COPPER_CASING.isIn(stack)) {
            return BeltCasingKind.COPPER;
        }
        if (AllBlocks.RAILWAY_CASING.isIn(stack)
                || hasCasingKind(stack, CasingSets.INDUSTRIAL_IRON)
                || hasCasingKind(stack, CasingSets.SHADOW_STEEL)
                || hasCasingKind(stack, CasingSets.CREATIVE)
                || hasCasingKind(stack, CasingSets.WEATHERED_IRON)
                || hasCasingKind(stack, CasingSets.REFINED_RADIANCE)) {
            return BeltCasingKind.COPPER;
        }
        return BeltCasingKind.NONE;
    }

    private static boolean hasCasingKind(ItemStack stack, CasingSet casingSet) {
        var casing = casingSet.getCasing();
        return casing != null && stack.is(casing.asItem());
    }

    @Override
    public boolean isBelt(BlockState state) {
        return AllBlocks.BELT.has(state);
    }

    @Override
    public boolean applyBeltCasing(
            Level level,
            BlockPos pos,
            BlockState state,
            BeltCasingKind kind,
            Player player
    ) {
        if (!(state.getBlock() instanceof BeltBlock beltBlock)) {
            return false;
        }

        if (!(level.getBlockEntity(pos) instanceof BeltBlockEntity beltEntity)) {
            return false;
        }

        BeltBlockEntity.CasingType createCasingType = switch (kind) {
            case ANDESITE -> BeltBlockEntity.CasingType.ANDESITE;
            case BRASS -> BeltBlockEntity.CasingType.BRASS;
            default -> BeltBlockEntity.CasingType.NONE;
        };

        if (beltEntity.casing == createCasingType) {
            return false;
        }

        beltEntity.setCasingType(createCasingType);
        beltBlock.updateCoverProperty(level, pos, level.getBlockState(pos));
        beltEntity.setChanged();

        var updatedState = level.getBlockState(pos);
        level.sendBlockUpdated(pos, state, updatedState, Block.UPDATE_ALL);
        return true;
    }

    @Override
    public boolean isManualPipe(BlockState state) {
        return AllBlocks.FLUID_PIPE.has(state) || AllBlocks.ENCASED_FLUID_PIPE.has(state);
    }

    @Override
    public boolean isPlainFluidPipe(BlockState state) {
        return AllBlocks.FLUID_PIPE.has(state);
    }

    @Override
    public void beforePipeStateChange(Level level, BlockPos pos) {
        FluidTransportBehaviour.cacheFlows(level, pos);
    }

    @Override
    public void afterPipeStateChange(Level level, BlockPos pos) {
        FluidTransportBehaviour.loadFlows(level, pos);
    }

    @Override
    public ChainActionResult applyWrench(
            BlockState state,
            UseOnContext context,
            boolean sneaking
    ) {
        if (!(state.getBlock() instanceof IWrenchable wrenchable)) {
            return ChainActionResult.PASS;
        }

        InteractionResult result = sneaking
                ? wrenchable.onSneakWrenched(state, context)
                : wrenchable.onWrenched(state, context);

        return result.consumesAction() ? ChainActionResult.SUCCESS : ChainActionResult.PASS;
    }

}
