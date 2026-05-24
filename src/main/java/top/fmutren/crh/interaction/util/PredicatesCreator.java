package top.fmutren.crh.interaction.util;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import fr.iglee42.createcasing.casings.CasingSets;
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

        if(loadCreateCasing){
            boolean isCreateCasingShaft = crhCreateCasingIsCasingShaft(state);
            if(isCreateCasingShaft) return true;
        }

        if(AllBlocks.ANDESITE_ENCASED_SHAFT.has(state)) return true;
        return AllBlocks.BRASS_ENCASED_SHAFT.has(state);
    }

    public static boolean isEncasedCogwheel(BlockState state) {

        if(loadCreateCasing){
            boolean isCreateCasingShaft = crhCreateCasingIsCasingCogwheel(state);
            if(isCreateCasingShaft) return true;
        }

        if(AllBlocks.ANDESITE_ENCASED_COGWHEEL.has(state)) return true;
        if(AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL.has(state)) return true;
        if(AllBlocks.BRASS_ENCASED_LARGE_COGWHEEL.has(state)) return true;
        return AllBlocks.BRASS_ENCASED_COGWHEEL.has(state);
    }

    public static  boolean isEncasedPipe(BlockState state){
        if(AllBlocks.ENCASED_FLUID_PIPE.has(state)) return true;
        if(loadCreateCasing){
            boolean isCreateCasingPipi = crhCreateCasingIsCasingPipe(state);
            if(isCreateCasingPipi) return true;
        }
        return false;
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
        return StateSwitch.commonSwitchForHeldItem(stack) == 1;
    }

    public static BeltBlockEntity.CasingType beltCasingType(ItemStack stack) {
        if (AllBlocks.ANDESITE_CASING.isIn(stack)) return BeltBlockEntity.CasingType.ANDESITE;

        if (AllBlocks.BRASS_CASING.isIn(stack)) return BeltBlockEntity.CasingType.BRASS;

        if(loadCreateCasing){
            if(AllBlocks.COPPER_CASING.isIn(stack)) return CasingSets.COPPER.getBeltCasingType();

            if(AllBlocks.RAILWAY_CASING.isIn(stack)) return CasingSets.RAILWAY.getBeltCasingType();

            if(CasingSets.INDUSTRIAL_IRON.getCasing().asItem().equals(stack.getItem())) return CasingSets.INDUSTRIAL_IRON.getBeltCasingType();

            if(CasingSets.SHADOW_STEEL.getCasing().asItem().equals(stack.getItem())) return CasingSets.SHADOW_STEEL.getBeltCasingType();

            if(CasingSets.CREATIVE.getCasing().asItem().equals(stack.getItem())) return CasingSets.CREATIVE.getBeltCasingType();

            if(CasingSets.WEATHERED_IRON.getCasing().asItem().equals(stack.getItem())) return CasingSets.WEATHERED_IRON.getBeltCasingType();

            if(CasingSets.REFINED_RADIANCE.getCasing().asItem().equals(stack.getItem())) return CasingSets.REFINED_RADIANCE.getBeltCasingType();
        }

        return null;
    }
}
