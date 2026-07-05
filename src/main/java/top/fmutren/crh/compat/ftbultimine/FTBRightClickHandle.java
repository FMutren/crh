package top.fmutren.crh.compat.ftbultimine;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import dev.ftb.mods.ftbultimine.FTBUltiminePlayerData;
import dev.ftb.mods.ftbultimine.shape.ShapeContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;

import static top.fmutren.crh.interaction.StateSwitch.iterationTypeForItem;
import static top.fmutren.crh.interaction.TryToEncase.tryToEncaseAllType;
import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;


public class FTBRightClickHandle {
    public static int FTBRightClickEventHandler(ShapeContext context,
                                                InteractionHand hand,
                                                FTBUltiminePlayerData data,
                                                Direction face
    ) {
        Player player = context.player();
        if(player.isSpectator() || !player.mayBuild()) return 0;
        Level level = player.level();
        ItemStack heldItem = player.getItemInHand(hand);
        Collection<BlockPos> positions = data.cachedPositions();

        int count = 0;

        switch (iterationTypeForItem(heldItem)) {
            case WRENCH -> {
                for (BlockPos pos : positions) {
                    BlockState state = level.getBlockState(pos);
                    if (state.getBlock() instanceof IWrenchable wrenchable) {

                        UseOnContext useOnContext = new UseOnContext(level,
                                player,
                                hand,
                                heldItem,
                                centerHit(pos, face));

                        if (player.isShiftKeyDown()) {
                            wrenchable.onSneakWrenched(state, useOnContext);
                        } else {
                            wrenchable.onWrenched(state, useOnContext);
                        }
                        count++;
                    }
                }
            }
            case COMMON_CASING, PIPE_CASING, CHUTE_CASING -> {
                if(player.isShiftKeyDown()) return 0;
                for (BlockPos pos : positions) {
                    BlockState state = level.getBlockState(pos);
                    if(!tryToEncaseAllType(state, level, pos, player, hand, heldItem)) return 0;
                }
                count++;
            }
        }
        return count;
    }
}

