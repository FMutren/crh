package top.fmutren.crh.interaction.util;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import top.fmutren.crh.api.BeltCasingKind;
import top.fmutren.crh.api.CrhServices;
import top.fmutren.crh.interaction.ChainSelection;

import java.util.function.Predicate;

import static top.fmutren.crh.CrhCommon.loadCreateCasing;
import static top.fmutren.crh.compat.createcasing.CrhCreateCasingCompat.crhCreateCasingPredicate;
import static top.fmutren.crh.interaction.StateSwitch.isCasing;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isEncasedPipe;

public final class TargetSelector {

    private TargetSelector() {
    }

    public static ChainSelection selectEncasing(
            Level level,
            BlockPos pos,
            BlockState state,
            ItemStack stack
    ) {
        if (isCasing(stack) && AllBlocks.FLUID_PIPE.has(state)) {
            if (AllBlocks.COPPER_CASING.isIn(stack) || loadCreateCasing) {
                return ChainCollector.collectPipe(
                        level,
                        pos,
                        AllBlocks.FLUID_PIPE::has,
                        CrhServices.platform().maxPipeBlocks()
                );
            }
        }

        if (PredicatesCreator.isShaftCasing(stack) && AllBlocks.SHAFT.has(state)) {
            var axis = state.getValue(ShaftBlock.AXIS);
            return ChainCollector.collectShaft(
                    level,
                    pos,
                    axis,
                    AllBlocks.SHAFT::has,
                    CrhServices.platform().maxShaftBlocks()
            );
        }

        var casingKind = CrhServices.create().beltCasingKind(stack);
        if (casingKind != BeltCasingKind.NONE && AllBlocks.BELT.has(state)) {
            return ChainCollector.collectBelt(
                    level,
                    pos,
                    CrhServices.platform().maxBeltBlocks(),
                    belt -> belt.casing != BeltBlockEntity.CasingType.NONE
            );
        }

        return ChainSelection.empty();
    }

    public static ChainSelection select(
            Level level,
            BlockPos pos,
            BlockState state,
            boolean sneaking
    ) {
        if (!sneaking && isEncasedPipe(state)) {
            Predicate<BlockState> predicate = AllBlocks.ENCASED_FLUID_PIPE::has;
            if (loadCreateCasing && !AllBlocks.ENCASED_FLUID_PIPE.has(state)) {
                predicate = crhCreateCasingPredicate(state);
            }
            return ChainCollector.collectPipe(level, pos, predicate, CrhServices.platform().maxPipeBlocks());
        }

        if (!sneaking && PredicatesCreator.isBeltWithCasing(level, pos, state)) {
            return ChainCollector.collectBelt(
                    level,
                    pos,
                    CrhServices.platform().maxBeltBlocks(),
                    belt -> belt.casing != BeltBlockEntity.CasingType.NONE
            );
        }

        if (sneaking && PredicatesCreator.isEncasedShaft(state)) {
            var axis = state.getValue(ShaftBlock.AXIS);
            return ChainCollector.collectShaft(
                    level,
                    pos,
                    axis,
                    PredicatesCreator::isEncasedShaft,
                    CrhServices.platform().maxShaftBlocks()
            );
        }

        return ChainSelection.empty();
    }

}
