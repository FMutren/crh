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

import static top.fmutren.crh.crh_forge.loadCreateCasing;
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

    public static boolean crhCreateCasingIsCasingCogwheel(BlockState state){
        for(CasingSet casingSets : CASING){
            if(casingSets.getCogwheel().equals(state.getBlock()) ||
            casingSets.getLargeCogwheel().equals(state.getBlock())) return true;
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
                "creative_casing", "createcasing:creative_encased_shaft",
                "railway_casing", "createcasing:railway_encased_shaft",
                "copper_casing", "createcasing:copper_encased_shaft",
                "industrial_iron_block", "createcasing:industrial_iron_encased_shaft",
                "shadow_steel_casing",  "createcasing:shadow_steel_encased_shaft",
                "refined_radiance_casing", "createcasing:refined_radiance_encased_shaft",
                "weathered_iron_casing", "createcasing:weathered_encased_shaft")
        );
    }

    public static void crhCreatePipeCasingType(){
        pipeCasingType.putAll(Map.of(
                "creative_casing", "createcasing:creative_encased_fluid_pipe",
                "railway_casing", "createcasing:railway_encased_fluid_pipe",
                "industrial_iron_block", "createcasing:industrial_iron_encased_fluid_pipe",
                "shadow_steel_casing",  "createcasing:shadow_steel_encased_fluid_pipe",
                "refined_radiance_casing", "createcasing:refined_radiance_encased_fluid_pipe",
                "andesite_casing", "createcasing:andesite_encased_fluid_pipe",
                "brass_casing", "createcasing:brass_encased_fluid_pipe",
                "weathered_iron_casing", "createcasing:weathered_encased_fluid_pipe")
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
