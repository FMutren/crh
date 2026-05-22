package top.fmutren.crh.interaction;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

import static top.fmutren.crh.interaction.util.PredicatesCreator.isShaftCasing;

public class StateSwitch {
    private static final Map<String, String> shaftCasingType = new HashMap<>();
    static {
        shaftCasingType.put("create:brass_casing", "create:brass_encased_shaft");
        shaftCasingType.put("create:andesite_casing", "create:andesite_encased_shaft");
    }

    public static BlockState shaftSwitchToBlockState(ItemStack itemStack, BlockState state) {
        if(itemStack.isEmpty()) return state;
        if(!isShaftCasing(itemStack)) return state;
        String result = shaftCasingType.get(itemStack.getItem().toString());
        if(result == null) return state;
        return BuiltInRegistries.BLOCK.get(ResourceLocation
                .parse(result))
                .defaultBlockState()
                .setValue(ShaftBlock.AXIS, state.getValue(ShaftBlock.AXIS));
    }

    private static final Map<String, String> pipeCasingType = new HashMap<>();
    static {
        pipeCasingType.put("create:copper_casing", "create:encased_fluid_pipe");
    }

    public static BlockState pipeSwitchToBlockState(ItemStack itemStack, BlockState state) {
        if(itemStack.isEmpty()) return state;
        if(!AllBlocks.COPPER_CASING.isIn(itemStack)) return state;
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

    public static BlockState beltSwitchToBlockState(ItemStack itemStack, BlockState state) {
        if(itemStack.isEmpty()) return state;
        if(!isShaftCasing(itemStack)) return state;
        return BuiltInRegistries.BLOCK.get(ResourceLocation
                .parse("create:belt"))
                .defaultBlockState()
                .setValue(BeltBlock.HORIZONTAL_FACING, state.getValue(BeltBlock.HORIZONTAL_FACING))
                .setValue(BeltBlock.CASING, Boolean.TRUE)
                .setValue(BeltBlock.PART, state.getValue(BeltBlock.PART));
    }
}
