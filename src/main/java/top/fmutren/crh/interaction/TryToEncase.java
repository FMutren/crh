package top.fmutren.crh.interaction;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import top.fmutren.crh.interaction.util.PredicatesCreator;

import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isCommonCasing;

public class TryToEncase {

    private TryToEncase(){}

    public static boolean tryToEncaseAllType(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack heldItem) {
        return tryToEncaseCommonType(state, level, pos, player, hand, heldItem) ||
                tryToEncaseChute(level, pos, player, heldItem) ||
                tryToEncaseBelt(heldItem, pos, level);
    }

    public static boolean tryToEncaseCommonType(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack heldItem) {
        if(!isCommonCasing(heldItem)) return false;
        if(!(state.getBlock() instanceof EncasableBlock encasableBlock)) return false;

        encasableBlock.tryEncase(state, level, pos, heldItem, player, hand, centerHit(pos, Direction.UP));
        return true;
    }

    public static boolean tryToEncaseChute(Level level, BlockPos pos, Player player, ItemStack heldItem) {
        BlockState state = level.getBlockState(pos);
        if(!AllBlocks.INDUSTRIAL_IRON_BLOCK.isIn(heldItem)) return false;
        if(!(state.getBlock() instanceof ChuteBlock)) return false;

        if(state.getValue(ChuteBlock.SHAPE).equals(ChuteBlock.Shape.ENCASED) ||
                state.getValue(ChuteBlock.SHAPE).equals(ChuteBlock.Shape.INTERSECTION)) return false;

        BlockState newState = state.setValue(ChuteBlock.SHAPE, ChuteBlock.Shape.ENCASED);
        level.setBlockAndUpdate(pos, newState);
        playPlacedSound(AllBlocks.INDUSTRIAL_IRON_BLOCK, pos, level, player);
        return  true;
    }

    public static boolean tryToEncaseBelt(ItemStack heldItem, BlockPos pos, Level level) {
        if(!isCommonCasing(heldItem)) return false;
        if(!(level.getBlockState(pos).getBlock() instanceof BeltBlock beltBlock)) return false;


        BeltBlockEntity.CasingType casingType = PredicatesCreator.beltCasingType(heldItem);
        if (casingType == null) return false;
        beltBlock.withBlockEntityDo(level, pos, be -> be.setCasingType(casingType));
        beltBlock.updateCoverProperty(level, pos, level.getBlockState(pos));
        return true;
    }

    public static void playPlacedSound(BlockEntry<? extends Block> blockEntry, BlockPos pos, Level level, Player player) {
        SoundType soundType = blockEntry.getDefaultState()
                .getSoundType(level, pos, player);
        level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
    }
}
