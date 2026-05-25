package top.fmutren.crh.compat.createcasing;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.function.Predicate;

public final class CrhCreateCasingCompat {

    private static CreateCasingBridge bridge = CreateCasingBridge.NOOP;

    private CrhCreateCasingCompat() {
    }

    public static void bootstrap(CreateCasingBridge createCasingBridge) {
        bridge = createCasingBridge == null ? CreateCasingBridge.NOOP : createCasingBridge;
    }

    public static boolean isLoaded() {
        return bridge.isLoaded();
    }

    public static int casingSwitch(ItemStack itemStack) {
        return bridge.casingSwitch(itemStack);
    }

    public static boolean crhCreateCasingIsCasingShaft(BlockState state) {
        return bridge.isCasingShaft(state);
    }

    public static boolean crhCreateCasingIsCasingCogwheel(BlockState state) {
        return bridge.isCasingCogwheel(state);
    }

    public static boolean crhCreateCasingIsCasingPipe(BlockState state) {
        return bridge.isCasingPipe(state);
    }

    public static void crhCreateCasingShaftCasingType(Map<String, String> shaftCasingType) {
        bridge.addShaftCasingTypes(shaftCasingType);
    }

    public static void crhCreatePipeCasingType(Map<String, String> pipeCasingType) {
        bridge.addPipeCasingTypes(pipeCasingType);
    }

    public static Predicate<BlockState> crhCreateCasingPredicate(BlockState originState) {
        return bridge.casingPipePredicate(originState);
    }

    public static BeltBlockEntity.CasingType beltCasingType(ItemStack itemStack) {
        return bridge.beltCasingType(itemStack);
    }

    public static Block casingSoundBlock(ItemStack itemStack) {
        return bridge.casingSoundBlock(itemStack);
    }

}
