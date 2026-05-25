package top.fmutren.crh.compat.createcasing;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSets;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public final class CreateCasingBridgeImpl implements CreateCasingBridge {

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

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public int casingSwitch(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return -1;
        }

        for (var casingSet : CASING_ITEMS) {
            if (isCasingItem(itemStack, casingSet)) {
                return 1;
            }
        }

        return -1;
    }

    @Override
    public boolean isCasingPipe(BlockState state) {
        return isOneOf(state, CasingSet::getFluidPipe, PIPE_CASING_SETS);
    }

    @Override
    public boolean isCasingShaft(BlockState state) {
        return isOneOf(state, CasingSet::getShaft, SHAFT_CASING_SETS);
    }

    @Override
    public boolean isCasingCogwheel(BlockState state) {
        return isOneOf(state, CasingSet::getCogwheel, COGWHEEL_CASING_SETS)
                || isOneOf(state, CasingSet::getLargeCogwheel, COGWHEEL_CASING_SETS);
    }

    @Override
    public void addShaftCasingTypes(Map<String, String> shaftCasingType) {
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

    @Override
    public void addPipeCasingTypes(Map<String, String> pipeCasingType) {
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

    @Override
    public Predicate<BlockState> casingPipePredicate(BlockState originState) {
        if (!isCasingPipe(originState)) {
            return state -> false;
        }

        var originBlock = originState.getBlock();
        return state -> state != null && state.getBlock() == originBlock;
    }

    @Override
    public BeltBlockEntity.CasingType beltCasingType(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return null;
        }

        if (AllBlocks.COPPER_CASING.isIn(itemStack)) {
            return CasingSets.COPPER.getBeltCasingType();
        }

        if (AllBlocks.RAILWAY_CASING.isIn(itemStack)) {
            return CasingSets.RAILWAY.getBeltCasingType();
        }

        for (var casingSet : BELT_CASING_SETS) {
            if (isCasingItem(itemStack, casingSet)) {
                return casingSet.getBeltCasingType();
            }
        }

        return null;
    }

    @Override
    public Block casingSoundBlock(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return null;
        }

        if (AllBlocks.COPPER_CASING.isIn(itemStack)) {
            return AllBlocks.COPPER_CASING.get();
        }

        if (AllBlocks.RAILWAY_CASING.isIn(itemStack)) {
            return AllBlocks.RAILWAY_CASING.get();
        }

        for (var casingSet : BELT_CASING_SETS) {
            if (!isCasingItem(itemStack, casingSet)) {
                continue;
            }

            var casing = casingSet.getCasing();
            return casing;
        }

        return null;
    }

    private static boolean isOneOf(
            BlockState state,
            Function<CasingSet, Block> blockGetter,
            CasingSet... casingSets
    ) {
        if (state == null || blockGetter == null || casingSets == null) {
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

    private static boolean isCasingItem(ItemStack itemStack, CasingSet casingSet) {
        if (itemStack == null || itemStack.isEmpty() || casingSet == null) {
            return false;
        }

        var casing = casingSet.getCasing();
        return casing != null && itemStack.is(casing.asItem());
    }

}
