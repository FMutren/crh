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
import top.fmutren.crh.interaction.util.PredicatesCreator;

import javax.annotation.Nullable;

import static top.fmutren.crh.Config.enableView;
import static top.fmutren.crh.input.RightClick.ENCASE_MAPPING;

@Mixin(PlacementClient.class)
public class CheckHelpersMixin {
    @Shadow
    static void setTarget(@Nullable BlockPos target){}

    @Inject(
            method = "checkHelpers",
            at = @At("HEAD")
    )
    private static void checkHelpers(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel world = mc.level;

        if (world == null)
            return;

        if (!(mc.hitResult instanceof BlockHitResult ray))
            return;

        if (mc.player == null)
            return;

        if (mc.player.isShiftKeyDown())
            return;

        BlockPos pos = ray.getBlockPos();

        if(world.getBlockState(pos).getBlock() instanceof EncasableBlock && ENCASE_MAPPING.get().isDown() && enableView()) {
            if (PredicatesCreator.isCasing(mc.player.getMainHandItem())) {
                setTarget(pos);
            }
        }
    }
}
