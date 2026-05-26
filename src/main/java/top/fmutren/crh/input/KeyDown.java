package top.fmutren.crh.input;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.fmutren.crh.Config;
import top.fmutren.crh.interaction.ChainRender;
import top.fmutren.crh.interaction.util.ChainKeyStateTracker;
import top.fmutren.crh.network.ModMessages;

import static top.fmutren.crh.input.RightClick.ENCASE_MAPPING;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isCasing;
import static top.fmutren.crh.interaction.util.PredicatesCreator.isWrench;


public final class KeyDown {

    private static boolean lastSentChainKeyState;

    private KeyDown() {
    }



    public static void syncChainKeyState(Player player) {
        if (!Config.builtinChainAllowed()) {
            ChainKeyStateTracker.set(player, false);
            if (lastSentChainKeyState) {
                PacketDistributor.sendToServer(new ModMessages.ChainKeyStatePayload(false));
                lastSentChainKeyState = false;
            }
            return;
        }

        boolean chainKeyDown = ENCASE_MAPPING.get().isDown();
        ChainKeyStateTracker.set(player, chainKeyDown);

        if (chainKeyDown == lastSentChainKeyState) return;

        PacketDistributor.sendToServer(new ModMessages.ChainKeyStatePayload(chainKeyDown));
        lastSentChainKeyState = chainKeyDown;
    }

}
