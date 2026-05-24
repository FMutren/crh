package top.fmutren.crh.mixin;

import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import net.createmod.catnip.placement.PlacementClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fmutren.crh.interaction.StateSwitch;

import javax.annotation.Nullable;

import static top.fmutren.crh.Config.enableView;
import static top.fmutren.crh.input.RightClick.ENCASE_MAPPING;

@Mixin(PlacementClient.class)
public class CheckHelpersMixin {

    @Inject(
            method = "checkHelpers",
            at = @At("HEAD")
    )
    private static void checkHelpers(CallbackInfo ci) {
        var minecraft = Minecraft.getInstance();
        ClientLevel world = minecraft.level;

        if (world == null || minecraft.player == null || minecraft.player.isShiftKeyDown()) {
            return;
        }

        if (!(minecraft.hitResult instanceof BlockHitResult ray)) {
            return;
        }

        var pos = ray.getBlockPos();
        boolean canShowView = world.getBlockState(pos).getBlock() instanceof EncasableBlock
                && ENCASE_MAPPING.get().isDown()
                && enableView();

        if (canShowView && StateSwitch.commonSwitchForHeldItem(minecraft.player.getMainHandItem()) != -1) {
            setTarget(pos);
        }
    }

    @Shadow
    static void setTarget(@Nullable BlockPos target) {
    }

}
