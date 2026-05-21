package top.fmutren.crh.compat.ftbultimine;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import dev.ftb.mods.ftbultimine.api.shape.Shape;
import dev.ftb.mods.ftbultimine.api.shape.ShapeContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import top.fmutren.crh.Config;
import top.fmutren.crh.Crh;
import top.fmutren.crh.interaction.ChainSelection;
import top.fmutren.crh.interaction.util.ChainCollector;
import top.fmutren.crh.interaction.util.PredicatesCreator;

import java.util.List;

public enum CrhChainShape implements Shape {

    INSTANCE;

    private static final ResourceLocation ID = Crh.id("chain");

    @Override
    public ResourceLocation getName() {
        return ID;
    }

    @Override
    public List<BlockPos> getBlocks(ShapeContext context) {
        if (context == null || context.player() == null) {
            return List.of();
        }

        var player = context.player();
        var level = player.level();
        BlockPos origin = context.origPos();
        BlockState state = context.origState();

        if (origin == null || state == null || !level.isLoaded(origin)) {
            return List.of();
        }

        ItemStack stack = interactionStack(player);

        if (stack.isEmpty() && PredicatesCreator.isManualPipe(state)) {
            return positions(ChainCollector.collectPipe(
                    level,
                    origin,
                    PredicatesCreator::isManualPipe,
                    limit(context, Config.maxPipeBlocks())
            ));
        }

        if (AllBlocks.COPPER_CASING.isIn(stack) && AllBlocks.FLUID_PIPE.has(state)) {
            return positions(ChainCollector.collectPipe(
                    level,
                    origin,
                    AllBlocks.FLUID_PIPE::has,
                    limit(context, Config.maxPipeBlocks())
            ));
        }

        if (PredicatesCreator.isShaftCasing(stack) && AllBlocks.SHAFT.has(state)) {
            Direction.Axis axis = state.getValue(ShaftBlock.AXIS);

            return positions(ChainCollector.collectShaft(
                    level,
                    origin,
                    axis,
                    AllBlocks.SHAFT::has,
                    limit(context, Config.maxShaftBlocks())
            ));
        }

        BeltBlockEntity.CasingType beltCasingType = PredicatesCreator.beltCasingType(stack);
        if (beltCasingType != null && AllBlocks.BELT.has(state)) {
            return positions(ChainCollector.collectBelt(
                    level,
                    origin,
                    limit(context, Config.maxBeltBlocks()),
                    belt -> belt.casing != beltCasingType
            ));
        }

        if (isCreateWrench(stack)) {
            return wrenchSelection(level, origin, state, player.isShiftKeyDown(), context).positions();
        }

        return List.of();
    }

    @Override
    public boolean isIndeterminateShape() {
        return true;
    }

    private static ItemStack interactionStack(ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty()) {
            return mainHand;
        }

        return player.getOffhandItem();
    }

    private static List<BlockPos> positions(ChainSelection selection) {
        if (selection == null || selection.isEmpty()) {
            return List.of();
        }

        return selection.positions();
    }

    private static int limit(ShapeContext context, int crhLimit) {
        return Math.clamp(crhLimit, 0, context.maxBlocks());
    }

    private static boolean isCreateWrench(ItemStack stack) {
        return AllItems.WRENCH.isIn(stack) || stack.getItem() instanceof WrenchItem;
    }

    private static ChainSelection wrenchSelection(
            Level level,
            BlockPos origin,
            BlockState state,
            boolean sneaking,
            ShapeContext context
    ) {
        if (!sneaking && AllBlocks.ENCASED_FLUID_PIPE.has(state)) {
            return ChainCollector.collectPipe(
                    level,
                    origin,
                    AllBlocks.ENCASED_FLUID_PIPE::has,
                    limit(context, Config.maxPipeBlocks())
            );
        }

        if (!sneaking && PredicatesCreator.isBeltWithCasing(level, origin, state)) {
            return ChainCollector.collectBelt(
                    level,
                    origin,
                    limit(context, Config.maxBeltBlocks()),
                    belt -> belt.casing != BeltBlockEntity.CasingType.NONE
            );
        }

        if (sneaking && PredicatesCreator.isEncasedShaft(state)) {
            Direction.Axis axis = state.getValue(ShaftBlock.AXIS);

            return ChainCollector.collectShaft(
                    level,
                    origin,
                    axis,
                    PredicatesCreator::isEncasedShaft,
                    limit(context, Config.maxShaftBlocks())
            );
        }

        return ChainSelection.empty();
    }

}
