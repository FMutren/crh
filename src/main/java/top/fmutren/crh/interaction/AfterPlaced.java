package top.fmutren.crh.interaction;

import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.pipes.AxisPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static top.fmutren.crh.interaction.PlayerLookOnFace.fluidPipeFace;
import static top.fmutren.crh.interaction.TryToEncase.tryToEncaseAllType;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;
import static top.fmutren.crh.interaction.util.PredicatesCreator.*;

public class AfterPlaced {

    private  AfterPlaced(){}

    public static void tryAutoEncase(BlockState state, Level level, BlockPos pos, Player player) {
        ItemStack heldItem = player.getOffhandItem();
        if(heldItem.isEmpty()) return;
        if(!isCasing(heldItem)) return;
        if(!(state.getBlock() instanceof EncasableBlock) &&
                !(state.getBlock() instanceof ChuteBlock)) return;
        tryToEncaseAllType(state, level, pos, player, InteractionHand.OFF_HAND, heldItem);
    }

    public static void tryAutoOpenWindow(BlockState state, Level level, BlockPos pos, Player player) {
        ItemStack heldItem = player.getOffhandItem();
        if(heldItem.isEmpty()) return;
        if(!isWrench(heldItem)) return;
        if(state.getBlock() instanceof IWrenchable wrenchAble) {
            UseOnContext context = new UseOnContext(
                    level,
                    player,
                    InteractionHand.OFF_HAND,
                    heldItem,
                    centerHit(pos, getFluidDirection(state))
            );
            if(state.getBlock() instanceof ChuteBlock) wrenchAble.onWrenched(state, context);
            if(state.getBlock() instanceof FluidPipeBlock){
                BlockState newState = BuiltInRegistries.BLOCK.get(
                        ResourceLocation.parse("create:glass_fluid_pipe"))
                            .defaultBlockState()
                            .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED))
                            .setValue(AxisPipeBlock.AXIS, fluidPipeFace(player, state));
                level.setBlockAndUpdate(pos, newState);
            }
        }
    }

    private static Direction getFluidDirection(BlockState state) {
        if(!(state.getBlock() instanceof PipeBlock)) return Direction.UP;
        for (Direction direction : Direction.values()) {
            if (!isPipeOpen(state, direction)) return direction;
        }
        return Direction.UP;
    }
}
