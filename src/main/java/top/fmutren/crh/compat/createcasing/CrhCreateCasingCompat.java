package top.fmutren.crh.compat.createcasing;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSets;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static top.fmutren.crh.Crh.loadCreateCasing;

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

    @Nullable
    public static BlockState crhCreateCasingBeltCasingTypeToCasing(BeltBlockEntity.CasingType casingType){
        if(!loadCreateCasing) return null;
        for (CasingSet casingSet : CASING) {
            if (casingType == casingSet.getBeltCasingType()) return casingSet.getCasing().defaultBlockState();
        }
        return null;
    }

    @Nullable
    public static BeltBlockEntity.CasingType crhCreateCasingBeltCasingType(ItemStack stack){
        for(CasingSet casingSets : CASING){
            if(casingSets.getCasing().asItem().equals(stack.getItem())) return casingSets.getBeltCasingType();
        }
        return null;
    }
}
