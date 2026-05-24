package top.fmutren.crh.compat.createcasing;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSets;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static top.fmutren.crh.CrhCommon.loadCreateCasing;
import static top.fmutren.crh.interaction.StateSwitch.pipeCasingType;
import static top.fmutren.crh.interaction.StateSwitch.shaftCasingType;

public final class CrhCreateCasingCompat {

    private static final CasingSet[] CASING_ITEMS = {
            CasingSets.WEATHERED_IRON,
            CasingSets.CREATIVE,
            CasingSets.SHADOW_STEEL,
            CasingSets.INDUSTRIAL_IRON,
            CasingSets.REFINED_RADIANCE,
            CasingSets.COPPER,
            CasingSets.RAILWAY
    };
    private static final CasingSet[] BELT_CASING_SETS = {
            CasingSets.CREATIVE,
            CasingSets.RAILWAY,
            CasingSets.INDUSTRIAL_IRON,
            CasingSets.REFINED_RADIANCE,
            CasingSets.COPPER,
            CasingSets.SHADOW_STEEL,
            CasingSets.WEATHERED_IRON
    };
    private static final CasingSet[] SHAFT_CASING_SETS = {
            CasingSets.REFINED_RADIANCE,
            CasingSets.CREATIVE,
            CasingSets.SHADOW_STEEL,
            CasingSets.WEATHERED_IRON,
            CasingSets.INDUSTRIAL_IRON,
            CasingSets.RAILWAY,
            CasingSets.COPPER
    };
    private static final CasingSet[] PIPE_CASING_SETS = {
            CasingSets.SHADOW_STEEL,
            CasingSets.ANDESITE,
            CasingSets.CREATIVE,
            CasingSets.RAILWAY,
            CasingSets.INDUSTRIAL_IRON,
            CasingSets.WEATHERED_IRON,
            CasingSets.REFINED_RADIANCE,
            CasingSets.BRASS
    };
    private static final CasingSet[] COGWHEEL_CASING_SETS = {
            CasingSets.REFINED_RADIANCE,
            CasingSets.CREATIVE,
            CasingSets.SHADOW_STEEL,
            CasingSets.WEATHERED_IRON,
            CasingSets.INDUSTRIAL_IRON,
            CasingSets.RAILWAY,
            CasingSets.COPPER
    };

    private CrhCreateCasingCompat() {
    }

    public static int casingSwitch(ItemStack itemStack) {
        if (!loadCreateCasing || itemStack == null || itemStack.isEmpty()) {
            return -1;
        }

        for (var casingSet : CASING_ITEMS) {
            if (isCasingItem(itemStack, casingSet)) {
                return 1;
            }
        }

        return -1;
    }

    private static boolean isCasingItem(ItemStack itemStack, CasingSet casingSet) {
        if (casingSet == null) {
            return false;
        }

        var casing = casingSet.getCasing();
        return casing != null && itemStack.is(casing.asItem());
    }

    public static BlockState beltCasingTypeToCasing(BeltBlockEntity.CasingType casingType) {
        if (!loadCreateCasing || casingType == null) {
            return null;
        }

        for (var casingSet : BELT_CASING_SETS) {
            if (casingType != casingSet.getBeltCasingType()) {
                continue;
            }

            var casing = casingSet.getCasing();
            return casing == null ? null : casing.defaultBlockState();
        }

        return null;
    }

    public static boolean crhCreateCasingIsCasingShaft(BlockState state) {
        return isOneOf(state, CasingSet::getShaft, SHAFT_CASING_SETS);
    }

    private static boolean isOneOf(
            BlockState state,
            Function<CasingSet, Block> blockGetter,
            CasingSet... casingSets
    ) {
        if (!loadCreateCasing || state == null || blockGetter == null || casingSets == null) {
            return false;
        }

        var block = state.getBlock();

        for (var casingSet : casingSets) {
            if (casingSet == null) {
                continue;
            }

            var targetBlock = blockGetter.apply(casingSet);
            if (targetBlock != null && block == targetBlock) {
                return true;
            }
        }

        return false;
    }

    public static boolean crhCreateCasingIsCasingCogwheel(BlockState state) {
        return isOneOf(state, CasingSet::getCogwheel, COGWHEEL_CASING_SETS)
                || isOneOf(state, CasingSet::getLargeCogwheel, COGWHEEL_CASING_SETS);
    }

    public static void crhCreateCasingShaftCasingType() {
        shaftCasingType.putAll(Map.of(
                "createcasing:creative_casing", "createcasing:creative_encased_shaft",
                "create:railway_casing", "createcasing:railway_encased_shaft",
                "create:copper_casing", "createcasing:copper_encased_shaft",
                "create:industrial_iron_block", "createcasing:industrial_iron_encased_shaft",
                "createcasing:shadow_steel_casing", "createcasing:shadow_steel_encased_shaft",
                "createcasing:refined_radiance_casing", "createcasing:refined_radiance_encased_shaft",
                "createcasing:weathered_iron_casing", "createcasing:weathered_encased_shaft"
        ));
    }

    public static void crhCreatePipeCasingType() {
        pipeCasingType.putAll(Map.of(
                "createcasing:creative_casing", "createcasing:creative_encased_fluid_pipe",
                "create:railway_casing", "createcasing:railway_encased_fluid_pipe",
                "create:industrial_iron_block", "createcasing:industrial_iron_encased_fluid_pipe",
                "createcasing:shadow_steel_casing", "createcasing:shadow_steel_encased_fluid_pipe",
                "createcasing:refined_radiance_casing", "createcasing:refined_radiance_encased_fluid_pipe",
                "create:andesite_casing", "createcasing:andesite_encased_fluid_pipe",
                "create:brass_casing", "createcasing:brass_encased_fluid_pipe",
                "createcasing:weathered_iron_casing", "createcasing:weathered_encased_fluid_pipe"
        ));
    }

    public static Predicate<BlockState> crhCreateCasingPredicate(BlockState originState) {
        if (!crhCreateCasingIsCasingPipe(originState)) {
            return state -> false;
        }

        var originBlock = originState.getBlock();
        return state -> state != null && state.getBlock() == originBlock;
    }

    public static boolean crhCreateCasingIsCasingPipe(BlockState state) {
        return isOneOf(state, CasingSet::getFluidPipe, PIPE_CASING_SETS);
    }

}
