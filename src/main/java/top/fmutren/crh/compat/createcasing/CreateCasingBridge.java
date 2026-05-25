package top.fmutren.crh.compat.createcasing;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.function.Predicate;

public interface CreateCasingBridge {

    CreateCasingBridge NOOP = new CreateCasingBridge() {
    };

    default boolean isLoaded() {
        return false;
    }

    default int casingSwitch(ItemStack itemStack) {
        return -1;
    }

    default boolean isCasingPipe(BlockState state) {
        return false;
    }

    default boolean isCasingShaft(BlockState state) {
        return false;
    }

    default boolean isCasingCogwheel(BlockState state) {
        return false;
    }

    default void addShaftCasingTypes(Map<String, String> shaftCasingType) {
    }

    default void addPipeCasingTypes(Map<String, String> pipeCasingType) {
    }

    default Predicate<BlockState> casingPipePredicate(BlockState originState) {
        return state -> false;
    }

    default BeltBlockEntity.CasingType beltCasingType(ItemStack itemStack) {
        return null;
    }

    default Block casingSoundBlock(ItemStack itemStack) {
        return null;
    }

}
