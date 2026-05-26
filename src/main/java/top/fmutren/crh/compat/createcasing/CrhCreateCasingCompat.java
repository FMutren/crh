package top.fmutren.crh.compat.createcasing;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import fr.iglee42.createcasing.casings.CasingSets;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

import static top.fmutren.crh.Crh.loadCreateCasing;
import static top.fmutren.crh.interaction.StateSwitch.pipeCasingType;
import static top.fmutren.crh.interaction.StateSwitch.shaftCasingType;

public class CrhCreateCasingCompat {
    public static int CasingSwitch(ItemStack itemStack){
        if (CasingSets.WEATHERED_IRON.getCasing().asItem().equals(itemStack.getItem())) return 1;
        if (CasingSets.CREATIVE.getCasing().asItem().equals(itemStack.getItem())) return 1;
        if (CasingSets.SHADOW_STEEL.getCasing().asItem().equals(itemStack.getItem())) return 1;
        if (CasingSets.INDUSTRIAL_IRON.getCasing().asItem().equals(itemStack.getItem())) return 1;
        if (CasingSets.REFINED_RADIANCE.getCasing().asItem().equals(itemStack.getItem())) return 1;
        if (CasingSets.COPPER.getCasing().asItem().equals(itemStack.getItem())) return 1;
        if (CasingSets.RAILWAY.getCasing().asItem().equals(itemStack.getItem())) return 1;
        return -1;
    }

    public static BlockState BeltCasingTypeToCasing(BeltBlockEntity.CasingType casingType){
        if(!loadCreateCasing) return null;
        if(casingType == CasingSets.CREATIVE.getBeltCasingType()) return CasingSets.CREATIVE.getCasing().defaultBlockState();
        if(casingType == CasingSets.RAILWAY.getBeltCasingType()) return CasingSets.RAILWAY.getCasing().defaultBlockState();
        if(casingType == CasingSets.INDUSTRIAL_IRON.getBeltCasingType()) return CasingSets.INDUSTRIAL_IRON.getCasing().defaultBlockState();
        if(casingType == CasingSets.REFINED_RADIANCE.getBeltCasingType()) return CasingSets.REFINED_RADIANCE.getCasing().defaultBlockState();
        if(casingType == CasingSets.COPPER.getBeltCasingType()) return CasingSets.COPPER.getCasing().defaultBlockState();
        return null;
    }

    public static boolean crhCreateCasingIsCasingShaft(BlockState state){
        if(CasingSets.REFINED_RADIANCE.getShaft().equals(state.getBlock())) return true;
        if(CasingSets.CREATIVE.getShaft().equals(state.getBlock())) return true;
        if(CasingSets.SHADOW_STEEL.getShaft().equals(state.getBlock())) return true;
        if(CasingSets.WEATHERED_IRON.getShaft().equals(state.getBlock())) return true;
        if(CasingSets.INDUSTRIAL_IRON.getShaft().equals(state.getBlock())) return true;
        if(CasingSets.RAILWAY.getShaft().equals(state.getBlock())) return true;
        return CasingSets.COPPER.getShaft().equals(state.getBlock());
    }

    public static  boolean crhCreateCasingIsCasingPipe(BlockState state){
        if(CasingSets.SHADOW_STEEL.getFluidPipe().equals(state.getBlock())) return true;
        if(CasingSets.ANDESITE.getFluidPipe().equals(state.getBlock())) return true;
        if(CasingSets.CREATIVE.getFluidPipe().equals(state.getBlock())) return true;
        if(CasingSets.RAILWAY.getFluidPipe().equals(state.getBlock())) return true;
        if(CasingSets.INDUSTRIAL_IRON.getFluidPipe().equals(state.getBlock())) return true;
        if(CasingSets.WEATHERED_IRON.getFluidPipe().equals(state.getBlock())) return true;
        if(CasingSets.REFINED_RADIANCE.getFluidPipe().equals(state.getBlock())) return true;
        return (CasingSets.BRASS.getFluidPipe().equals(state.getBlock()));
    }

    public static void crhCreateCasingShaftCasingType(){
        shaftCasingType.put("createcasing:creative_casing", "createcasing:creative_encased_shaft");
        shaftCasingType.put("create:railway_casing", "createcasing:railway_encased_shaft");
        shaftCasingType.put("create:copper_casing", "createcasing:copper_encased_shaft");
        shaftCasingType.put("create:industrial_iron_block", "createcasing:industrial_iron_encased_shaft");
        shaftCasingType.put("createcasing:shadow_steel_casing",  "createcasing:shadow_steel_encased_shaft");
        shaftCasingType.put("createcasing:refined_radiance_casing", "createcasing:refined_radiance_encased_shaft");
        shaftCasingType.put("createcasing:weathered_iron_casing", "createcasing:weathered_encased_shaft");
    }

    public static void crhCreatePipeCasingType(){
        pipeCasingType.put("createcasing:creative_casing", "createcasing:creative_encased_fluid_pipe");
        pipeCasingType.put("create:railway_casing", "createcasing:railway_encased_fluid_pipe");
        pipeCasingType.put("create:industrial_iron_block", "createcasing:industrial_iron_encased_fluid_pipe");
        pipeCasingType.put("createcasing:shadow_steel_casing",  "createcasing:shadow_steel_encased_fluid_pipe");
        pipeCasingType.put("createcasing:refined_radiance_casing", "createcasing:refined_radiance_encased_fluid_pipe");
        pipeCasingType.put("create:andesite_casing", "createcasing:andesite_encased_fluid_pipe");
        pipeCasingType.put("create:brass_casing", "createcasing:brass_encased_fluid_pipe");
        pipeCasingType.put("createcasing:weathered_iron_casing", "createcasing:weathered_encased_fluid_pipe");
    }

    public static Predicate<BlockState> crhCreateCasingPredicate(BlockState state){
        return bState -> state.getBlock() == CasingSets.WEATHERED_IRON.getFluidPipe() ||
                state.getBlock() == CasingSets.RAILWAY.getFluidPipe() ||
                state.getBlock() == CasingSets.CREATIVE.getFluidPipe() ||
                state.getBlock() == CasingSets.REFINED_RADIANCE.getFluidPipe() ||
                state.getBlock() == CasingSets.ANDESITE.getFluidPipe() ||
                state.getBlock() == CasingSets.BRASS.getFluidPipe() ||
                state.getBlock() == CasingSets.SHADOW_STEEL.getFluidPipe() ||
                state.getBlock() == CasingSets.INDUSTRIAL_IRON.getFluidPipe();
    }
}
