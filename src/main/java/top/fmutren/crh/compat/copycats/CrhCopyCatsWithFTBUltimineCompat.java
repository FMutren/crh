package top.fmutren.crh.compat.copycats;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static top.fmutren.crh.interaction.util.ChainOperation.centerHit;

public class CrhCopyCatsWithFTBUltimineCompat {
    public static void FTBRightClickEventHandler() {
        RegisterRightClickHandlerEvent.REGISTER.register(registry ->
                registry.registerHandler((context,
                                          hand,
                                          positions) ->
                {
                    Player player = context.player();
                    Direction face = context.face();
                    if(player.isSpectator() || !player.mayBuild()) return 0;
                    Level level = player.level();
                    ItemStack heldItem = player.getItemInHand(hand);

                    int count = 0;

                    if(heldItem.getItem() instanceof BlockItem) {
                        for(BlockPos pos : positions) {
                            BlockState targetState = level.getBlockState(pos);
                            if(targetState.getBlock() instanceof ICopycatBlock icb) {
                                icb.useItemOn(heldItem, targetState, level, pos, player, hand, centerHit(pos, face));
                                count++;
                            }

                        }
                    }

                    return count;
                }));
    }
}
