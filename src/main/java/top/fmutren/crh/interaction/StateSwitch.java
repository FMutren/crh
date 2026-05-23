package top.fmutren.crh.interaction;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

import static top.fmutren.crh.Crh.loadCreateCasing;
import static top.fmutren.crh.compat.createcasing.CrhCreateCasingCompat.*;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isShaftCasing;

public class StateSwitch {
    public static final Map<String, String> shaftCasingType = new HashMap<>();
    static {
        shaftCasingType.put("create:brass_casing", "create:brass_encased_shaft");
        shaftCasingType.put("create:andesite_casing", "create:andesite_encased_shaft");

        if(loadCreateCasing){
            crhCreateCasingShaftCasingType();
        }
    }

    public static BlockState shaftSwitchToBlockState(ItemStack itemStack, BlockState state) {
        if(itemStack.isEmpty()) return state;
        if(!isShaftCasing(itemStack)) return state;
        if(!(state.getBlock() instanceof ShaftBlock)) return state;
        String result = shaftCasingType.get(itemStack.getItem().toString());
        if(result == null) return state;
        return BuiltInRegistries.BLOCK.get(ResourceLocation
                .parse(result))
                .defaultBlockState()
                .setValue(ShaftBlock.AXIS, state.getValue(ShaftBlock.AXIS));
    }

    public static final Map<String, String> pipeCasingType = new HashMap<>();
    static {
        pipeCasingType.put("create:copper_casing", "create:encased_fluid_pipe");

        if(loadCreateCasing){
            crhCreatePipeCasingType();
        }
    }

    public static BlockState pipeSwitchToBlockState(ItemStack itemStack, BlockState state) {
        if(itemStack.isEmpty()) return state;
        if(!AllBlocks.COPPER_CASING.isIn(itemStack) && !loadCreateCasing) return state;
        if(loadCreateCasing && !isCasing(itemStack)) return state;
        if(!(state.getBlock() instanceof PipeBlock)) return state;
        String result = pipeCasingType.get(itemStack.getItem().toString());
        if(result == null) return state;
        return BuiltInRegistries.BLOCK.get(ResourceLocation
                .parse(result))
                .defaultBlockState()
                .setValue(PipeBlock.UP , state.getValue(PipeBlock.UP))
                .setValue(PipeBlock.DOWN , state.getValue(PipeBlock.DOWN))
                .setValue(PipeBlock.WEST , state.getValue(PipeBlock.WEST))
                .setValue(PipeBlock.EAST , state.getValue(PipeBlock.EAST))
                .setValue(PipeBlock.NORTH , state.getValue(PipeBlock.NORTH))
                .setValue(PipeBlock.SOUTH , state.getValue(PipeBlock.SOUTH));
    }

    public static  int commonSwitchForHeldItem(ItemStack itemStack){
        if(isCreateWrench(itemStack)) return 0;

        if(AllBlocks.ANDESITE_CASING.isIn(itemStack)) return 1;
        if(AllBlocks.BRASS_CASING.isIn(itemStack)) return 1;

        if(loadCreateCasing) {
            return CasingSwitch(itemStack);
        }

        if(AllBlocks.COPPER_CASING.isIn(itemStack)) return 2;

        if(AllBlocks.INDUSTRIAL_IRON_BLOCK.isIn(itemStack)) return 3;

        return -1;
    }

    public static boolean isCasing(ItemStack itemStack) {
        return(commonSwitchForHeldItem(itemStack) != 0 && commonSwitchForHeldItem(itemStack) != -1);
    }

    public static boolean isCreateWrench(ItemStack stack) {
        return stack.getItem() instanceof WrenchItem;
    }
}
