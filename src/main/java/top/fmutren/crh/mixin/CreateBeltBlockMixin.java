package top.fmutren.crh.mixin;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//import top.fmutren.crh.interaction.ChainInteraction;

@Mixin(BeltBlock.class)
public abstract class CreateBeltBlockMixin {


//    @Inject(
//            method = "useItemOn",
//            at = @At("TAIL"),
//            locals = LocalCapture.CAPTURE_FAILSOFT,
//            cancellable = true
//    )
//    private static void crh$BeltAutoCasing(
//            ItemStack stack,
//            BlockState state,
//            Level level,
//            BlockPos pos,
//            Player player,
//            InteractionHand hand,
//            BlockHitResult hitResult,
//            CallbackInfoReturnable<ItemInteractionResult> cir,
//            boolean isConnector
//    )
//    {
//        System.out.println("1");
//        if (isConnector) BeltSlicer.useConnector(state, level, pos, player, hand, hitResult, new BeltSlicer.Feedback());
//        System.out.println("2");
//        ItemStack helOffHandItemStack = player.getOffhandItem();
//        if(!isInShaftCasing(helOffHandItemStack))return;
//        System.out.println("3");
//        ItemInteractionResult result = ChainInteraction.tryHandleEncasing(
//                helOffHandItemStack,
//                state,
//                level,
//                pos,
//                player,
//                InteractionHand.OFF_HAND,
//                hitResult
//        );
//        if (result.consumesAction()) {
//            cir.setReturnValue(ItemInteractionResult.SUCCESS);
//        }
//    }
}
