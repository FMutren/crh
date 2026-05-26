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
import static top.fmutren.crh.interaction.util.PredicatesCreator.isCommonCasing;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isPipeCasing;
import static top.fmutren.crh.render.OuterContourRender.renderGhostBlock;

public class ChainRender {
    private boolean isLookFirst = false;
    private ChainSelection chainSelection;

    public ChainRender() {
    }

    public void getToRender(Level level, BlockPos pos, ItemStack item) {
        ChainSelection selection = chainSelectionToRender(level, pos, item);
        if (selection == null) return;
        renderChainGhostBlock(selection, item, level);
    }

    private BlockState stateSwitch(BlockState state, ItemStack item) {
        switch (state.getBlock()) {
            case ShaftBlock ignored -> {
                return shaftSwitchToBlockState(item, state);
            }
            case PipeBlock ignored -> {
                return pipeSwitchToBlockState(item, state);
            }
            default -> {
                return state;
            }
        }
    }

    private void renderChainGhostBlock(ChainSelection selection, ItemStack itemStack, Level level) {
        if(selection.isEmpty()) return;
        for(BlockPos pos : selection.positions()){
            BlockState blockState = level.getBlockState(pos);
            BlockState state = stateSwitch(blockState, itemStack);
            if(blockState == state) return;
            renderGhostBlock(pos, state);
        }
    }

    private ChainSelection chainSelectionToRender(Level level, BlockPos pos, ItemStack item){
        BlockState state = level.getBlockState(pos);
        boolean isEncasableBlock = (state.getBlock() instanceof EncasableBlock);
        if(isEncasableBlock != isLookFirst){
            isLookFirst = isEncasableBlock;
            if(isLookFirst){
                switch (state.getBlock()) {
                    case ShaftBlock ignored -> {
                        if(!isCommonCasing(item)) return ChainSelection.empty();
                        chainSelection = ChainCollector.collectShaft(
                                level,
                                pos,
                                state.getValue(ShaftBlock.AXIS),
                                AllBlocks.SHAFT::has,
                                Config.maxShaftBlocks()
                        );
                    }
                    case PipeBlock ignored -> {
                        if(!isPipeCasing(item)) return ChainSelection.empty();
                        chainSelection = ChainCollector.collectPipe(
                                level,
                                pos,
                                AllBlocks.FLUID_PIPE::has,
                                Config.maxPipeBlocks()
                        );
                    }
                    default -> {
                        return ChainSelection.empty();
                    }
                }
            }
        }
        if(isEncasableBlock) {
            return chainSelection;
        }
        else return ChainSelection.empty();
    }
}
