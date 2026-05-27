package top.fmutren.crh.mixin.compat.ftbcompat;

import dev.ftb.mods.ftbultimine.FTBUltiminePlayerData;
import dev.ftb.mods.ftbultimine.shape.ShapeContext;
import dev.ftb.mods.ftbultimine.utils.forge.PlatformMethodsImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static top.fmutren.crh.compat.ftbultimine.FTBRightClickHandle.FTBRightClickEventHandler;

@Pseudo
@Mixin(value = PlatformMethodsImpl.class, remap = false)
public final class FTBUltimineCompatMixin {

    @Inject(
            method = "blockRightClick",
            at = @At("TAIL")
    )
    private static void crhFTBBlockRightClickCompat(ShapeContext shapeContext,
                                                    ServerPlayer serverPlayer,
                                                    InteractionHand hand,
                                                    BlockPos clickPos,
                                                    Direction face,
                                                    FTBUltiminePlayerData data,
                                                    CallbackInfoReturnable<Integer> cir)
    {
        FTBRightClickEventHandler(shapeContext, hand, data);
    }
}
