package top.fmutren.crh.mixin;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.simibubi.create.content.equipment.wrench.IWrenchable.playRemoveSound;

@Mixin(IWrenchable.class)
public interface CreateIWrenchableFixMixn {
    @Inject(
            method = "onSneakWrenched",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onSneakWrenched(
            BlockState state,
            UseOnContext context,
            CallbackInfoReturnable<InteractionResult> cir
    ){
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (world instanceof ServerLevel serverLevel) {

            BlockState blockState = serverLevel.getBlockState(pos);
            blockState.getBlock().playerWillDestroy(world,pos, blockState, player);

            if (player != null && !player.isCreative()) {
                Block.getDrops(state, serverLevel, pos, world.getBlockEntity(pos), player, context.getItemInHand())
                        .forEach(itemStack -> {
                            player.getInventory()
                                    .placeItemBackInInventory(itemStack);
                        });
            }

            state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, true);
            world.destroyBlock(pos, false);
            playRemoveSound(world, pos);
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
