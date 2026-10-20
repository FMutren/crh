package top.fmutren.crh.interaction;

import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;

import static top.fmutren.crh.interaction.BuildCasingType.CASINGS;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isCasing;

public class StateSwitch {

    private StateSwitch() {}

    public static BlockState switchToBlockState(ItemStack itemStack, BlockState state) {
        if(itemStack.isEmpty()) return state;
        if(!isCasing(itemStack)) return state;
        if(!(state.getBlock() instanceof EncasableBlock)) return state;
        Block result = CASINGS.getOrDefault(itemStack.getItem(), Set.of()).stream()
                .filter(e -> e.base() == state.getBlock())
                .map(BuildCasingType.Encasing::variant).findFirst().orElse(null);
        if(result == null) return state;
        return copyCompatibleProperties(state, result.defaultBlockState());
    }

    private static BlockState copyCompatibleProperties(BlockState from, BlockState to) {
        for (Property<?> property : from.getProperties()) {
            if (to.hasProperty(property)) {
                to = copyProperty(from, to, property);
            }
        }
        return to;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(
            BlockState from, BlockState to, Property<T> property) {
        return to.setValue(property, from.getValue(property));
    }
}
