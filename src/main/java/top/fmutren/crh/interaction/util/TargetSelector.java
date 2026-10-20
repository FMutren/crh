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

import static top.fmutren.crh.interaction.util.PredicatesCreator.isEncasedPipe;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isPipeCasing;

public final class TargetSelector {

    private TargetSelector() {
    }

    public static ChainSelection selectEncasing(Level level, BlockPos pos, BlockState state, ItemStack stack) {
        if (AllBlocks.FLUID_PIPE.has(state)) {
            if(isPipeCasing(stack)) return ChainCollector.collectPipe(level, pos, AllBlocks.FLUID_PIPE::has, Config.maxChainBlocks());
        }

        if (PredicatesCreator.isCommonCasing(stack) && AllBlocks.SHAFT.has(state)) {
            Direction.Axis axis = state.getValue(ShaftBlock.AXIS);
            return ChainCollector.collectShaft(level, pos, axis, AllBlocks.SHAFT::has, Config.maxChainBlocks());
        }

        if (AllBlocks.INDUSTRIAL_IRON_BLOCK.isIn(stack) && AllBlocks.CHUTE.has(state)) {
            return ChainCollector.collectChute(level, pos, PredicatesCreator::isEncasableChute, Config.maxChainBlocks());
        }

        if (AllBlocks.BELT.has(state) && PredicatesCreator.isCasing(stack)) {
            return ChainCollector.collectBelt(level, pos, Config.maxChainBlocks(), belt -> true);
        }

        if (PredicatesCreator.isCommonCasing(stack) && AllBlocks.COGWHEEL.has(state)){
            return ChainCollector.collectCommon(level, pos, AllBlocks.COGWHEEL::has, Config.maxChainBlocks());
        }

        return ChainSelection.empty();
    }

    public static ChainSelection select(Level level, BlockPos pos, BlockState state, boolean sneaking) {
        if (!sneaking && isEncasedPipe(state)) {
            return ChainCollector.collectPipe(level, pos, PredicatesCreator::isEncasedPipe, Config.maxChainBlocks());
        }

        if (!sneaking && PredicatesCreator.isEncasedBelt(level, pos, state)) {
            return ChainCollector.collectBelt(
                    level,
                    pos,
                    Config.maxChainBlocks(),
                    belt -> belt.casing != BeltBlockEntity.CasingType.NONE
            );
        }

        if (!sneaking && PredicatesCreator.isEncasedChute(state)) {
            return ChainCollector.collectChute(level, pos, PredicatesCreator::isEncasedChute, Config.maxChainBlocks());
        }

        if (sneaking && PredicatesCreator.isEncasedShaft(state)) {
            Direction.Axis axis = state.getValue(ShaftBlock.AXIS);
            return ChainCollector.collectShaft(
                    level,
                    pos,
                    axis,
                    PredicatesCreator::isEncasedShaft,
                    Config.maxChainBlocks()
            );
        }

        if (sneaking && PredicatesCreator.isEncasedCogwheel(state)){
            return ChainCollector.collectCommon(
                    level,
                    pos,
                    PredicatesCreator::isEncasedCogwheel,
                    Config.maxChainBlocks()
            );
        }

        return ChainSelection.empty();
    }

}
