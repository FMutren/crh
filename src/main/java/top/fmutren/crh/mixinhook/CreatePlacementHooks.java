package top.fmutren.crh.mixinhook;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.fluids.pipes.AxisPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import top.fmutren.crh.interaction.StateSwitch;
import top.fmutren.crh.platform.ResourceLocations;

import static top.fmutren.crh.interaction.PlayerLookOnFace.fluidPipeFace;
import static top.fmutren.crh.interaction.PlayerLookOnFace.getPlayerLookingFace;
import static top.fmutren.crh.interaction.StateSwitch.isCreateWrench;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isShaftCasing;

public final class CreatePlacementHooks {

    private CreatePlacementHooks() {
    }

    public static void afterBlockPlaced(
            LevelAccessor levelAccessor,
            BlockPos pos,
            BlockState state,
            LivingEntity placer
    ) {
        if (!(levelAccessor instanceof Level level) || !(placer instanceof Player player) || pos == null || state == null) {
            return;
        }

        if (level.isClientSide) {
            return;
        }

        var heldOffHandItem = player.getOffhandItem();
        if (heldOffHandItem.isEmpty()) {
            return;
        }

        tryAutoEncaseShaft(level, pos, state, heldOffHandItem);
        tryAutoModifyChute(level, pos, state, player, heldOffHandItem);
        tryAutoEncaseCogwheel(level, pos, state, player, heldOffHandItem);
        tryAutoModifyFluidPipe(level, pos, state, player, heldOffHandItem);
    }

    private static void tryAutoEncaseShaft(
            Level level,
            BlockPos pos,
            BlockState state,
            net.minecraft.world.item.ItemStack heldOffHandItem
    ) {
        var newState = StateSwitch.shaftSwitchToBlockState(heldOffHandItem, state);
        if (newState != state) {
            level.setBlockAndUpdate(pos, newState);
        }
    }

    private static void tryAutoModifyChute(
            Level level,
            BlockPos pos,
            BlockState state,
            Player player,
            net.minecraft.world.item.ItemStack heldOffHandItem
    ) {
        if (!(state.getBlock() instanceof AbstractChuteBlock chuteBlock)) {
            return;
        }

        var facing = chuteBlock.getFacing(state);

        if (AllBlocks.INDUSTRIAL_IRON_BLOCK.isIn(heldOffHandItem)) {
            level.setBlockAndUpdate(pos, state.setValue(ChuteBlock.SHAPE, ChuteBlock.Shape.ENCASED));
            return;
        }

        if (AllItems.WRENCH.isIn(heldOffHandItem) && facing == Direction.DOWN) {
            level.setBlockAndUpdate(pos, state.setValue(ChuteBlock.SHAPE, ChuteBlock.Shape.WINDOW));
        }
    }

    private static void tryAutoEncaseCogwheel(
            Level level,
            BlockPos pos,
            BlockState state,
            Player player,
            net.minecraft.world.item.ItemStack heldOffHandItem
    ) {
        if (!(state.getBlock() instanceof CogWheelBlock) || !isShaftCasing(heldOffHandItem)
                || !(state.getBlock() instanceof EncasableBlock encasableBlock)) {
            return;
        }

        var face = getPlayerLookingFace(player);
        var blockHit = centerHit(pos, face);
        encasableBlock.tryEncase(
                state,
                level,
                pos,
                heldOffHandItem,
                player,
                InteractionHand.MAIN_HAND,
                blockHit
        );
    }

    private static void tryAutoModifyFluidPipe(
            Level level,
            BlockPos pos,
            BlockState state,
            Player player,
            net.minecraft.world.item.ItemStack heldOffHandItem
    ) {
        if (!(state.getBlock() instanceof FluidPipeBlock)) {
            return;
        }

        var face = getPlayerLookingFace(player);
        var blockHit = centerHit(pos, face);

        if (AllBlocks.COPPER_CASING.isIn(heldOffHandItem) && state.getBlock() instanceof EncasableBlock encasableBlock) {
            encasableBlock.tryEncase(
                    state,
                    level,
                    pos,
                    heldOffHandItem,
                    player,
                    InteractionHand.MAIN_HAND,
                    blockHit
            );
            return;
        }

        if (isCreateWrench(heldOffHandItem)) {
            BlockState newState = BuiltInRegistries.BLOCK.get(ResourceLocations.parse("create:glass_fluid_pipe"))
                    .defaultBlockState()
                    .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED))
                    .setValue(AxisPipeBlock.AXIS, fluidPipeFace(player, state));
            level.setBlockAndUpdate(pos, newState);
        }
    }

}
