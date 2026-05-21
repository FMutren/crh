package top.fmutren.crh.interaction;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import top.fmutren.crh.Config;
import top.fmutren.crh.interaction.util.ChainCollector;
import top.fmutren.crh.interaction.util.PredicatesCreator;

import static top.fmutren.crh.render.OuterContourRender.renderGhostBlock;

public class ChainRender {
    private boolean isLookFirst = false;
    private ChainSelection chainSelection;

    public ChainRender() {
    }

    public void GetToRender(Level level, BlockPos pos) {
        ChainSelection selection = ChainSelectionToRender(level, pos);
        if (selection == null) return;
        RenderChainGhostBlock(selection, level.getBlockState(pos));
    }

    private void RenderChainGhostBlock(ChainSelection selection, BlockState state){
        if(selection.isEmpty()) return;
        if(state.isEmpty()) return;
        for(BlockPos pos : selection.positions()){
            renderGhostBlock(pos, state);
        }
    }

    private ChainSelection ChainSelectionToRender(Level level, BlockPos pos){
        BlockState state = level.getBlockState(pos);
        boolean isEncasableBlock = (state.getBlock() instanceof EncasableBlock);
        if(isEncasableBlock != isLookFirst){
            isLookFirst = isEncasableBlock;
            if(isLookFirst){
                return switch (state.getBlock()) {
                    case ShaftBlock ignored -> chainSelection = ChainCollector.collectShaft(
                            level,
                            pos,
                            state.getValue(ShaftBlock.AXIS),
                            PredicatesCreator::isEncasedShaft,
                            Config.maxShaftBlocks()
                    );
                    case PipeBlock ignored -> chainSelection = ChainCollector.collectPipe(
                            level,
                            pos,
                            AllBlocks.ENCASED_FLUID_PIPE::has,
                            Config.maxPipeBlocks()
                    );
                    case BeltBlock ignored -> chainSelection = ChainCollector.collectBelt(
                            level,
                            pos,
                            Config.maxBeltBlocks(),
                            b -> b.casing != BeltBlockEntity.CasingType.NONE
                    );
                    default -> chainSelection;
                };
            }
        }
        if(isEncasableBlock) {
            return chainSelection;
        }
        else return null;
    }
}
