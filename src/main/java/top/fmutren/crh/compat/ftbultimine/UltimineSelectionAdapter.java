package top.fmutren.crh.compat.ftbultimine;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.fmutren.crh.interaction.ChainSelection;
import top.fmutren.crh.interaction.util.PredicatesCreator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class UltimineSelectionAdapter {

    private UltimineSelectionAdapter() {
    }

    public static ChainSelection forEncasing(
            Level level,
            Collection<BlockPos> positions,
            ItemStack stack
    ) {
        List<BlockPos> result = new ArrayList<>();

        for (BlockPos pos : positions) {
            if (!level.isLoaded(pos)) {
                continue;
            }

            var state = level.getBlockState(pos);

            if (AllBlocks.COPPER_CASING.isIn(stack) && AllBlocks.FLUID_PIPE.has(state)) {
                result.add(pos.immutable());
                continue;
            }

            if (PredicatesCreator.isShaftCasing(stack) && AllBlocks.SHAFT.has(state)) {
                result.add(pos.immutable());
                continue;
            }

            BeltBlockEntity.CasingType casingType = PredicatesCreator.beltCasingType(stack);
            if (casingType != null
                    && AllBlocks.BELT.has(state)
                    && level.getBlockEntity(pos) instanceof BeltBlockEntity belt
                    && belt.casing != casingType) {
                result.add(pos.immutable());
            }
        }

        return new ChainSelection(result, false);
    }

    public static ChainSelection forWrench(Level level, Collection<BlockPos> positions) {
        List<BlockPos> result = new ArrayList<>();

        for (var pos : positions) {
            if (!level.isLoaded(pos)) {
                continue;
            }

            var state = level.getBlockState(pos);

            if (AllBlocks.ENCASED_FLUID_PIPE.has(state)
                    || PredicatesCreator.isBeltWithCasing(level, pos, state)
                    || PredicatesCreator.isEncasedShaft(state)) {
                result.add(pos.immutable());
            }
        }

        return new ChainSelection(result, false);
    }

}
