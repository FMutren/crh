package top.fmutren.crh.compat.createcasing;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSets;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import top.fmutren.crh.interaction.StateSwitch;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Predicate;

import static top.fmutren.crh.Crh.loadCreateCasing;
import static top.fmutren.crh.interaction.StateSwitch.pipeCasingType;
import static top.fmutren.crh.interaction.StateSwitch.shaftCasingType;

public class CrhCreateCasingCompat {

    private CrhCreateCasingCompat(){}

    private static final CasingSet[] CASING = {
            CasingSets.WEATHERED_IRON,
            CasingSets.CREATIVE,
            CasingSets.SHADOW_STEEL,
            CasingSets.INDUSTRIAL_IRON,
            CasingSets.REFINED_RADIANCE,
            CasingSets.RAILWAY,
            CasingSets.COPPER,
            CasingSets.ANDESITE,
            CasingSets.BRASS
    };

    public static StateSwitch.iterationType CasingSwitch(ItemStack itemStack){
        for (CasingSet casingSet : CASING) {
            if (casingSet.getCasing().asItem().equals(itemStack.getItem())) return StateSwitch.iterationType.COMMON_CASING;
        }
        return StateSwitch.iterationType.UNKNOWN;
    }

    @Nullable
    public static BlockState crhCreateCasingBeltCasingTypeToCasing(BeltBlockEntity.CasingType casingType){
        if(!loadCreateCasing) return null;
        for (CasingSet casingSet : CASING) {
            if (casingType == casingSet.getBeltCasingType()) return casingSet.getCasing().defaultBlockState();
        }
        return null;
    }

    public static boolean crhCreateCasingIsCasingShaft(BlockState state){
        for(CasingSet casingSets : CASING){
            if(casingSets.getShaft().equals(state.getBlock())) return true;
        }
        return false;
    }

    public static boolean crhCreateCasingIsCasingCogWheel(BlockState state){
        for(CasingSet casingSets : CASING){
            if(casingSets.getCogwheel().equals(state.getBlock())) return true;
            if(casingSets.getLargeCogwheel().equals(state.getBlock())) return true;
        }
        return false;
    }

    public static  boolean crhCreateCasingIsCasingPipe(BlockState state){
        for(CasingSet casingSets : CASING){
            if(casingSets.getFluidPipe().equals(state.getBlock())) return true;
        }
        return false;
    }

    public static void crhCreateCasingShaftCasingType(){
        shaftCasingType.putAll(Map.of(
                "createcasing:creative_casing", "createcasing:creative_encased_shaft",
                "create:railway_casing", "createcasing:railway_encased_shaft",
                "create:copper_casing", "createcasing:copper_encased_shaft",
                "create:industrial_iron_block", "createcasing:industrial_iron_encased_shaft",
                "createcasing:shadow_steel_casing",  "createcasing:shadow_steel_encased_shaft",
                "createcasing:refined_radiance_casing", "createcasing:refined_radiance_encased_shaft",
                "createcasing:weathered_iron_casing", "createcasing:weathered_encased_shaft")
        );
    }

    public static void crhCreatePipeCasingType(){
        pipeCasingType.putAll(Map.of(
                "createcasing:creative_casing", "createcasing:creative_encased_fluid_pipe",
                "create:railway_casing", "createcasing:railway_encased_fluid_pipe",
                "create:industrial_iron_block", "createcasing:industrial_iron_encased_fluid_pipe",
                "createcasing:shadow_steel_casing",  "createcasing:shadow_steel_encased_fluid_pipe",
                "createcasing:refined_radiance_casing", "createcasing:refined_radiance_encased_fluid_pipe",
                "create:andesite_casing", "createcasing:andesite_encased_fluid_pipe",
                "create:brass_casing", "createcasing:brass_encased_fluid_pipe",
                "createcasing:weathered_iron_casing", "createcasing:weathered_encased_fluid_pipe")
        );
    }

    @Nullable
    public static BeltBlockEntity.CasingType crhCreateCasingBeltCasingType(ItemStack stack){
        for(CasingSet casingSets : CASING){
            if(casingSets.getCasing().asItem().equals(stack.getItem())) return casingSets.getBeltCasingType();
        }
        return null;
    }

    @Nullable
    public static Predicate<BlockState> crhCreateCasingPredicate(BlockState state){
        for(CasingSet casingSet : CASING) {
            if (state.getBlock() == casingSet.getFluidPipe())
                return bState -> state.getBlock() == casingSet.getFluidPipe();
        }
        return null;
    }
}
