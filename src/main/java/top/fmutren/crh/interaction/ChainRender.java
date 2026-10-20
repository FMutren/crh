package top.fmutren.crh.interaction;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.createmod.catnip.ghostblock.GhostBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import top.fmutren.crh.Config;
import top.fmutren.crh.interaction.util.ChainCollector;

import java.util.function.Predicate;

import static top.fmutren.crh.interaction.StateSwitch.switchToBlockState;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isCommonCasing;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isPipeCasing;

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
        if(state.getBlock() instanceof EncasableBlock){
            return switchToBlockState(item, state);
        }
        return state;
    }

    private void renderChainGhostBlock(ChainSelection selection, ItemStack itemStack, Level level) {
        if(selection.isEmpty()) return;
        for(BlockPos pos : selection.positions()){
            BlockState blockState = level.getBlockState(pos);
            BlockState state = stateSwitch(blockState, itemStack);
            if(blockState == state) continue;
            renderGhostBlock(pos, state);
        }
    }

    private static int id = 0;

    public static void renderGhostBlock(BlockPos pos, BlockState state){
        Object slot = "crh_view" + id++;
        GhostBlocks.getInstance()
                .showGhostState(slot, state)
                .at(pos)
                .breathingAlpha();
    }

    private ChainSelection chainSelectionToRender(Level level, BlockPos pos, ItemStack item){
        BlockState state = level.getBlockState(pos);
        boolean isEncasableBlock = (state.getBlock() instanceof EncasableBlock);
        if(isEncasableBlock != isLookFirst){
            isLookFirst = isEncasableBlock;
            if(isLookFirst){
                if(state.getBlock() instanceof ShaftBlock) {
                    if(!isCommonCasing(item)) return ChainSelection.empty();
                    chainSelection = ChainCollector.collectShaft(
                            level,
                            pos,
                            state.getValue(ShaftBlock.AXIS),
                            AllBlocks.SHAFT::has,
                            Config.maxChainBlocks()
                    );
                }
                else if(state.getBlock() instanceof PipeBlock) {
                    if(!isPipeCasing(item)) return ChainSelection.empty();
                    chainSelection = ChainCollector.collectPipe(
                            level,
                            pos,
                            AllBlocks.FLUID_PIPE::has,
                            Config.maxChainBlocks()
                    );
                }
                else {
                    Predicate<BlockState> target = t -> t.getBlock() == level.getBlockState(pos).getBlock();
                    chainSelection = ChainCollector.collectCommon(
                            level,
                            pos,
                            target,
                            Config.maxChainBlocks()
                    );
                }
            }
        }
        if(isEncasableBlock) {
            return chainSelection;
        }
        else return ChainSelection.empty();
    }
}
