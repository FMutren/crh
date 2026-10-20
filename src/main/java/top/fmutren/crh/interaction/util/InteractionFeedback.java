package top.fmutren.crh.interaction.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import top.fmutren.crh.interaction.ChainSelection;

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
}
