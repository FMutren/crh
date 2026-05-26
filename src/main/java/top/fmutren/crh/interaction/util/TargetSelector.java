package top.fmutren.crh.interaction.util;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import top.fmutren.crh.Config;
import top.fmutren.crh.interaction.ChainSelection;

import java.util.function.Predicate;

import static top.fmutren.crh.Crh.loadCreateCasing;
import static top.fmutren.crh.compat.createcasing.CrhCreateCasingCompat.crhCreateCasingPredicate;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isCasing;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isEncasedPipe;

public final class TargetSelector {

    private TargetSelector() {
    }

    public static ChainSelection selectEncasing(Level level, BlockPos pos, BlockState state, ItemStack stack) {
        if (isCasing(stack) && AllBlocks.FLUID_PIPE.has(state)) {
            if(AllBlocks.COPPER_CASING.has(state) || loadCreateCasing) return ChainCollector.collectPipe(level, pos, AllBlocks.FLUID_PIPE::has, Config.maxPipeBlocks());
        }

        if (PredicatesCreator.isCommonCasing(stack) && AllBlocks.SHAFT.has(state)) {
            Direction.Axis axis = state.getValue(ShaftBlock.AXIS);
            return ChainCollector.collectShaft(level, pos, axis, AllBlocks.SHAFT::has, Config.maxShaftBlocks());
        }

        BeltBlockEntity.CasingType casingType = PredicatesCreator.beltCasingType(stack);
        if (casingType != null && AllBlocks.BELT.has(state)) {
            return ChainCollector.collectBelt(level, pos, Config.maxBeltBlocks(), belt -> belt.casing != casingType);
        }

        return ChainSelection.empty();
    }

    public static ChainSelection select(Level level, BlockPos pos, BlockState state, boolean sneaking) {
        if (!sneaking && isEncasedPipe(state)) {
            Predicate<BlockState> predicate = AllBlocks.ENCASED_FLUID_PIPE::has;
            if(loadCreateCasing && !AllBlocks.ENCASED_FLUID_PIPE.has(state)) {
                predicate = crhCreateCasingPredicate(state);
                if (predicate == null) return ChainSelection.empty();
            }
            return ChainCollector.collectPipe(level, pos, predicate, Config.maxPipeBlocks());
        }

        if (!sneaking && PredicatesCreator.isEncasedBelt(level, pos, state)) {
            return ChainCollector.collectBelt(
                    level,
                    pos,
                    Config.maxBeltBlocks(),
                    belt -> belt.casing != BeltBlockEntity.CasingType.NONE
            );
        }

        if (sneaking && PredicatesCreator.isEncasedShaft(state)) {
            Direction.Axis axis = state.getValue(ShaftBlock.AXIS);
            return ChainCollector.collectShaft(
                    level,
                    pos,
                    axis,
                    PredicatesCreator::isEncasedShaft,
                    Config.maxShaftBlocks()
            );
        }

        return ChainSelection.empty();
    }

}
