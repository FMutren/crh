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
import org.spongepowered.asm.mixin.Overwrite;

import static com.simibubi.create.content.equipment.wrench.IWrenchable.playRemoveSound;

@Mixin(
        value = IWrenchable.class,
        remap = false
)
public interface CreateIWrenchableFixMixin {
    /**
     * @author FMutren
     * @reason to fix the block disappear when use wrench wich ftbultimine
     */
    @Overwrite(remap = false)
    default InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (world instanceof ServerLevel serverLevel) {
            state.getBlock().playerWillDestroy(serverLevel, pos, state, player);
            if (!player.isCreative()) {
                Block.getDrops(state, serverLevel, pos, world.getBlockEntity(pos), player, context.getItemInHand())
                        .forEach(itemStack ->
                            player.getInventory()
                                    .placeItemBackInInventory(itemStack)
                        );
            }
            state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, true);
            world.destroyBlock(pos, false);
            playRemoveSound(world, pos);
        }
        return InteractionResult.SUCCESS;
    }
}