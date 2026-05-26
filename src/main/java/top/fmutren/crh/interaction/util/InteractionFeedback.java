package top.fmutren.crh.interaction.util;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import top.fmutren.crh.interaction.ChainSelection;

import static top.fmutren.crh.compat.createcasing.CrhCreateCasingCompat.crhCreateCasingBeltCasingTypeToCasing;

public final class InteractionFeedback {

    private InteractionFeedback() {
    }

    public static void finish(Player player, InteractionHand hand, ChainSelection selection) {
        if (player != null && hand != null) {
            player.swing(hand, true);
        }
        notifyLimit(player, selection);
    }

    public static void notifyLimit(Player player, ChainSelection selection) {
        if (player != null && selection.truncated()) {
            player.displayClientMessage(
                    Component.translatable("crh.message.chain_limit", selection.positions().size())
                            .withStyle(ChatFormatting.YELLOW),
                    true
            );
        }
    }

    public static void playBeltCasingSound(
            Level level,
            Player player,
            BlockPos pos,
            BeltBlockEntity.CasingType casingType
    ) {

        BlockState soundState = switch (casingType) {
            case ANDESITE -> AllBlocks.ANDESITE_CASING.getDefaultState();
            case BRASS -> AllBlocks.BRASS_CASING.getDefaultState();
            case NONE -> null;
            default ->  crhCreateCasingBeltCasingTypeToCasing(casingType);

        };
        if (soundState == null) {
            return;
        }

        var soundType = soundState.getSoundType(level, pos, player);
        level.playSound(
                null,
                pos,
                soundType.getPlaceSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F
        );
    }
}
