package top.fmutren.crh.interaction.util;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class PredicatesCreator {

    private static final Direction[] DIRECTIONS = Direction.values();

    public static boolean isManualPipe(BlockState state) {
        return AllBlocks.FLUID_PIPE.has(state) || AllBlocks.ENCASED_FLUID_PIPE.has(state);
    }

    public static int countOpenPipeFaces(BlockState state) {
        int open = 0;
        for (Direction direction : DIRECTIONS) {
            if (isPipeOpen(state, direction)) {
                open++;
            }
        }
        return open;
    }

    public static boolean isPipeOpen(BlockState state, Direction direction) {
        BooleanProperty property = pipeProperty(direction);
        return property != null && state.hasProperty(property) && state.getValue(property);
    }

    public static BooleanProperty pipeProperty(Direction direction) {
        return PipeBlock.PROPERTY_BY_DIRECTION.get(direction);
    }

    public static boolean isEncasedShaft(BlockState state) {
        return AllBlocks.ANDESITE_ENCASED_SHAFT.has(state) || AllBlocks.BRASS_ENCASED_SHAFT.has(state);
    }

    public static boolean isBeltWithCasing(Level level, BlockPos pos, BlockState state) {
        if (!AllBlocks.BELT.has(state)) {
            return false;
        }

        if (state.hasProperty(BeltBlock.CASING) && state.getValue(BeltBlock.CASING)) {
            return true;
        }

        return level.getBlockEntity(pos) instanceof BeltBlockEntity belt
                && belt.casing != BeltBlockEntity.CasingType.NONE;
    }

    public static boolean isShaftCasing(ItemStack stack) {
        return AllBlocks.ANDESITE_CASING.isIn(stack) || AllBlocks.BRASS_CASING.isIn(stack);
    }

    public static BeltBlockEntity.CasingType beltCasingType(ItemStack stack) {
        if (AllBlocks.ANDESITE_CASING.isIn(stack)) {
            return BeltBlockEntity.CasingType.ANDESITE;
        }

        if (AllBlocks.BRASS_CASING.isIn(stack)) {
            return BeltBlockEntity.CasingType.BRASS;
        }

        return null;
    }

    public static boolean isCasing(ItemStack stack) {
        return AllBlocks.ANDESITE_CASING.isIn(stack) ||
                AllBlocks.BRASS_CASING.isIn(stack) ||
                AllBlocks.COPPER_CASING.isIn(stack);
    }

}
