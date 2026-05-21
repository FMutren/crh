package top.fmutren.crh.compat.ftbultimine;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import dev.ftb.mods.ftbultimine.api.rightclick.RightClickHandler;
import dev.ftb.mods.ftbultimine.api.shape.ShapeContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import top.fmutren.crh.Config;
import top.fmutren.crh.interaction.ChainInteraction;
import top.fmutren.crh.interaction.ChainSelection;
import top.fmutren.crh.interaction.util.ChainOperation;
import top.fmutren.crh.interaction.util.InteractionFeedback;

import java.util.Collection;

public enum CrhUltimineRightClickHandler implements RightClickHandler {

    INSTANCE;

    @Override
    public int handleRightClickBlock(
            ShapeContext shapeContext,
            InteractionHand hand,
            Collection<BlockPos> positions
    ) {
        if (!Config.compatFtbUltimine() || positions.isEmpty()) {
            return 0;
        }

        var player = shapeContext.player();
        if (player == null || player.isSpectator() || !player.mayBuild()) {
            return 0;
        }

        var level = player.level();
        BlockPos origin = shapeContext.origPos();
        if (!level.isLoaded(origin)) {
            return 0;
        }

        var stack = player.getItemInHand(hand);

        if (stack.isEmpty()) return ChainOperation.applyPipeSelectionFromUltimine(
                player,
                level,
                positions,
                shapeContext.face(),
                hand,
                player.isShiftKeyDown()
        );

        if (isCreateWrench(stack)) {
            ChainSelection selection = UltimineSelectionAdapter.forWrench(level, positions);
            if (selection.isEmpty()) {
                return 0;
            }

            UseOnContext context = new UseOnContext(
                    player,
                    hand,
                    ChainOperation.centerHit(origin, shapeContext.face())
            );

            ChainInteraction.ChainOperationResult result =
                    ChainOperation.applyWrench(context, selection);

            if (result.changedAny()) {
                InteractionFeedback.finish(player, hand, selection);
            }

            return result.changed();
        }

        ChainSelection selection = UltimineSelectionAdapter.forEncasing(
                level,
                positions,
                stack
        );

        if (selection.isEmpty()) {
            return 0;
        }

        ChainInteraction.ChainOperationResult result =
                ChainOperation.applyEncasing(
                        level,
                        player,
                        hand,
                        ChainOperation.centerHit(origin, shapeContext.face()),
                        stack,
                        selection
                );

        if (result.changedAny()) {
            InteractionFeedback.finish(player, hand, selection);
        }

        return result.changed();
    }

    private static boolean isCreateWrench(ItemStack stack) {
        return AllItems.WRENCH.isIn(stack) || stack.getItem() instanceof WrenchItem;
    }

}
