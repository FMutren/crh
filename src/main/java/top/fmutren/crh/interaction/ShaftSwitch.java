package top.fmutren.crh.interaction;

import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

import static top.fmutren.crh.interaction.util.PredicatesCreator.isShaftCasing;

public class ShaftSwitch {
    private static final Map<String, String> shaftCasingType = new HashMap<>();
    static {
        shaftCasingType.put("create:brass_casing", "create:brass_encased_shaft");
        shaftCasingType.put("create:andesite_casing", "create:andesite_encased_shaft");
    }

    public static BlockState ShaftSwitchToBlockState(ItemStack itemStack, BlockState state) {
        if(itemStack.isEmpty()) return state;
        if(!isShaftCasing(itemStack)) return state;
        String result = shaftCasingType.get(itemStack.getItem().toString());
        if(result == null) return state;
        return BuiltInRegistries.BLOCK.get(ResourceLocation
                .parse(result))
                .defaultBlockState()
                .setValue(ShaftBlock.AXIS, state.getValue(ShaftBlock.AXIS));
    }
}
