package top.fmutren.crh.interaction;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import top.fmutren.crh.Config;
import top.fmutren.crh.interaction.util.ChainCollector;

import static top.fmutren.crh.interaction.StateSwitch.pipeSwitchToBlockState;
import static top.fmutren.crh.interaction.StateSwitch.shaftSwitchToBlockState;
import static top.fmutren.crh.render.OuterContourRender.renderGhostBlock;

public final class ChainRender {

    private BlockPos lastOrigin;
    private ChainSelection chainSelection = ChainSelection.empty();

    public void getToRender(
            Level level,
            BlockPos pos,
            ItemStack item
    ) {
        var selection = chainSelectionToRender(level, pos);
        if (selection.isEmpty()) {
            return;
        }

        renderChainGhostBlock(selection, item, level);
    }

    private ChainSelection chainSelectionToRender(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof EncasableBlock)) {
            lastOrigin = null;
            chainSelection = ChainSelection.empty();
            return chainSelection;
        }

        if (pos.equals(lastOrigin)) {
            return chainSelection;
        }

        lastOrigin = pos.immutable();

        if (state.getBlock() instanceof ShaftBlock) {
            chainSelection = ChainCollector.collectShaft(
                    level,
                    pos,
                    state.getValue(ShaftBlock.AXIS),
                    AllBlocks.SHAFT::has,
                    Config.maxShaftBlocks()
            );
            return chainSelection;
        }

        if (state.getBlock() instanceof PipeBlock) {
            chainSelection = ChainCollector.collectPipe(
                    level,
                    pos,
                    AllBlocks.FLUID_PIPE::has,
                    Config.maxPipeBlocks()
            );
            return chainSelection;
        }

        chainSelection = ChainSelection.empty();
        return chainSelection;
    }

    private void renderChainGhostBlock(
            ChainSelection selection,
            ItemStack itemStack,
            Level level
    ) {
        for (var pos : selection.positions()) {
            var blockState = level.getBlockState(pos);
            var state = stateSwitch(blockState, itemStack);
            if (blockState == state) {
                continue;
            }
            renderGhostBlock(pos, state);
        }
    }

    private BlockState stateSwitch(BlockState state, ItemStack item) {
        if (state.getBlock() instanceof ShaftBlock) {
            return shaftSwitchToBlockState(item, state);
        }

        if (state.getBlock() instanceof PipeBlock) {
            return pipeSwitchToBlockState(item, state);
        }

        return state;
    }

}
