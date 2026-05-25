package top.fmutren.crh.platform;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
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
import top.fmutren.crh.CrhCommon;
import top.fmutren.crh.api.BeltCasingKind;
import top.fmutren.crh.api.ChainActionResult;
import top.fmutren.crh.api.CreateBridge;
import top.fmutren.crh.compat.createcasing.CrhCreateCasingCompat;

public abstract class AbstractCreateBridge implements CreateBridge {

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
    public final Item fluidPipeItem() {
        return fluidPipeItemCompat();
    }

    @Override
    public BeltCasingKind beltCasingKind(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return BeltCasingKind.NONE;
        }
        if (AllBlocks.ANDESITE_CASING.isIn(stack)) {
            return BeltCasingKind.ANDESITE;
        }
        if (AllBlocks.BRASS_CASING.isIn(stack)) {
            return BeltCasingKind.BRASS;
        }
        if (CrhCommon.loadCreateCasing && CrhCreateCasingCompat.beltCasingType(stack) != null) {
            return BeltCasingKind.COPPER;
        }
        return BeltCasingKind.NONE;
    }

    @Override
    public boolean isBeltCasingItem(ItemStack stack) {
        return beltCasingKind(stack) != BeltCasingKind.NONE;
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
        BeltBlockEntity.CasingType createCasingType = switch (kind) {
            case ANDESITE -> BeltBlockEntity.CasingType.ANDESITE;
            case BRASS -> BeltBlockEntity.CasingType.BRASS;
            default -> null;
        };

        return createCasingType != null && applyBeltCasingType(level, pos, state, createCasingType);
    }

    private static boolean applyBeltCasingType(
            Level level,
            BlockPos pos,
            BlockState state,
            BeltBlockEntity.CasingType createCasingType
    ) {
        if (!(state.getBlock() instanceof BeltBlock beltBlock)) {
            return false;
        }

        if (!(level.getBlockEntity(pos) instanceof BeltBlockEntity beltEntity)) {
            return false;
        }

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
        return AllBlocks.FLUID_PIPE.has(state)
                || AllBlocks.ENCASED_FLUID_PIPE.has(state)
                || (CrhCommon.loadCreateCasing && CrhCreateCasingCompat.crhCreateCasingIsCasingPipe(state));
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

    @Override
    public boolean isEncasedShaftOrCogwheel(BlockState state) {
        if (AllBlocks.ANDESITE_ENCASED_SHAFT.has(state)
                || AllBlocks.BRASS_ENCASED_SHAFT.has(state)) {
            return true;
        }
        if (AllBlocks.ANDESITE_ENCASED_COGWHEEL.has(state)
                || AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL.has(state)
                || AllBlocks.BRASS_ENCASED_LARGE_COGWHEEL.has(state)
                || AllBlocks.BRASS_ENCASED_COGWHEEL.has(state)) {
            return true;
        }
        if (CrhCommon.loadCreateCasing) {
            return CrhCreateCasingCompat.crhCreateCasingIsCasingShaft(state)
                    || CrhCreateCasingCompat.crhCreateCasingIsCasingCogwheel(state);
        }
        return false;
    }

    @Override
    public Item returnItemForState(BlockState state) {
        if (state.getBlock() instanceof EncasedPipeBlock
                || (CrhCommon.loadCreateCasing && CrhCreateCasingCompat.crhCreateCasingIsCasingPipe(state))) {
            return fluidPipeItem();
        }
        return state.getBlock().asItem();
    }

    protected abstract Item fluidPipeItemCompat();

    @Override
    public boolean tryBeltCasingFromItem(
            ItemStack heldItem,
            Level level,
            BlockPos pos,
            BlockState state,
            Player player
    ) {
        BeltBlockEntity.CasingType casingType = beltCasingType(heldItem);
        if (casingType == null) {
            return false;
        }

        if (!applyBeltCasingType(level, pos, state, casingType)) {
            return false;
        }

        playCasingSound(casingSoundBlock(heldItem), pos, level, player);
        return true;
    }

    private static BeltBlockEntity.CasingType beltCasingType(ItemStack heldItem) {
        if (heldItem == null || heldItem.isEmpty()) {
            return null;
        }
        if (AllBlocks.ANDESITE_CASING.isIn(heldItem)) {
            return BeltBlockEntity.CasingType.ANDESITE;
        }
        if (AllBlocks.BRASS_CASING.isIn(heldItem)) {
            return BeltBlockEntity.CasingType.BRASS;
        }
        if (!CrhCommon.loadCreateCasing) {
            return null;
        }
        return CrhCreateCasingCompat.beltCasingType(heldItem);
    }

    private static void playCasingSound(
            Block casingBlock,
            BlockPos pos,
            Level level,
            Player player
    ) {
        var soundType = casingBlock.defaultBlockState().getSoundType(level, pos, player);
        level.playSound(
                null,
                pos,
                soundType.getPlaceSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F
        );
    }

    private static Block casingSoundBlock(ItemStack heldItem) {
        if (heldItem == null || heldItem.isEmpty()) {
            return AllBlocks.ANDESITE_CASING.get();
        }
        if (AllBlocks.COPPER_CASING.isIn(heldItem)) {
            return AllBlocks.COPPER_CASING.get();
        }
        if (AllBlocks.BRASS_CASING.isIn(heldItem)) {
            return AllBlocks.BRASS_CASING.get();
        }
        if (AllBlocks.ANDESITE_CASING.isIn(heldItem)) {
            return AllBlocks.ANDESITE_CASING.get();
        }
        if (CrhCommon.loadCreateCasing) {
            var casingBlock = CrhCreateCasingCompat.casingSoundBlock(heldItem);
            if (casingBlock != null) {
                return casingBlock;
            }
        }
        return AllBlocks.ANDESITE_CASING.get();
    }

    @Override
    public boolean tryChuteEncasingWithItem(
            ItemStack heldItem,
            Level level,
            BlockPos pos,
            BlockState state,
            Player player
    ) {
        if (!AllBlocks.INDUSTRIAL_IRON_BLOCK.isIn(heldItem) || !CrhCommon.loadCreateCasing) {
            return false;
        }
        return tryApplyChuteEncasing(level, pos, state, player);
    }

    @Override
    public boolean tryApplyChuteEncasing(
            Level level,
            BlockPos pos,
            BlockState state,
            Player player
    ) {
        if (!(state.getBlock() instanceof ChuteBlock)) {
            return false;
        }
        var shape = state.getValue(ChuteBlock.SHAPE);
        if (shape == ChuteBlock.Shape.ENCASED || shape == ChuteBlock.Shape.INTERSECTION) {
            return false;
        }
        var newState = state.setValue(ChuteBlock.SHAPE, ChuteBlock.Shape.ENCASED);
        level.setBlockAndUpdate(pos, newState);
        playCasingSound(AllBlocks.INDUSTRIAL_IRON_BLOCK.get(), pos, level, player);
        return true;
    }

    @Override
    public boolean isChute(BlockState state) {
        return state.getBlock() instanceof ChuteBlock;
    }

}
