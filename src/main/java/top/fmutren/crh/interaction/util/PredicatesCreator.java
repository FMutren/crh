package top.fmutren.crh.interaction.util;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import top.fmutren.crh.interaction.StateSwitch;

import static top.fmutren.crh.Crh.loadCreateCasing;
import static top.fmutren.crh.compat.createcasing.CrhCreateCasingCompat.*;
import static top.fmutren.crh.interaction.StateSwitch.iterationTypeForItem;

public class PredicatesCreator {

    private static final Direction[] DIRECTIONS = Direction.values();

    public static boolean isManualPipe(BlockState state) {
        return AllBlocks.FLUID_PIPE.has(state) || isEncasedPipe(state);
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
        if(AllBlocks.ANDESITE_ENCASED_SHAFT.has(state) ||
                AllBlocks.BRASS_ENCASED_SHAFT.has(state)) return true;

        if(loadCreateCasing){
            return crhCreateCasingIsCasingShaft(state);
        }

        return false;
    }

    public static boolean isEncasedCogwheel(BlockState state) {
        if(AllBlocks.ANDESITE_ENCASED_COGWHEEL.has(state) ||
                AllBlocks.BRASS_ENCASED_COGWHEEL.has(state) ||
                AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL.has(state) ||
                AllBlocks.BRASS_ENCASED_LARGE_COGWHEEL.has(state)) return true;

        if(loadCreateCasing){
            return crhCreateCasingIsCasingCogWheel(state);
        }

        return false;
    }

    public static  boolean isEncasedPipe(BlockState state){
        if(AllBlocks.ENCASED_FLUID_PIPE.has(state)) return true;

        if(loadCreateCasing){
            return crhCreateCasingIsCasingPipe(state);
        }

        return false;
    }

    public static boolean isEncasedBelt(Level level, BlockPos pos, BlockState state) {
        if (!AllBlocks.BELT.has(state)) {
            return false;
        }

        if (state.hasProperty(BeltBlock.CASING) && state.getValue(BeltBlock.CASING)) {
            return true;
        }

        return level.getBlockEntity(pos) instanceof BeltBlockEntity belt
                && belt.casing != BeltBlockEntity.CasingType.NONE;
    }

    public static BeltBlockEntity.CasingType beltCasingType(ItemStack stack) {

        if (AllBlocks.ANDESITE_CASING.isIn(stack)) return BeltBlockEntity.CasingType.ANDESITE;
        if (AllBlocks.BRASS_CASING.isIn(stack)) return BeltBlockEntity.CasingType.BRASS;

        if(loadCreateCasing){
            return crhCreateCasingBeltCasingType(stack);
        }

        return null;
    }

    public static boolean isCommonCasing(ItemStack stack) {
        return iterationTypeForItem(stack) == StateSwitch.iterationType.COMMON_CASING;
    }

    public static boolean isPipeCasing(ItemStack stack) {
        return iterationTypeForItem(stack) == StateSwitch.iterationType.PIPE_CASING||
                iterationTypeForItem(stack) == StateSwitch.iterationType.COMMON_CASING &&
                loadCreateCasing;
    }

    public static boolean isCasing(ItemStack itemStack) {
        return(iterationTypeForItem(itemStack) != StateSwitch.iterationType.WRENCH &&
                iterationTypeForItem(itemStack) != StateSwitch.iterationType.UNKNOWN);
    }

    public static boolean isWrench(ItemStack stack) {
        return stack.getItem() instanceof WrenchItem;
    }
}
