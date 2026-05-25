package top.fmutren.crh.input;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import top.fmutren.crh.Config;
import top.fmutren.crh.api.CrhServices;
import top.fmutren.crh.interaction.ChainRender;
import top.fmutren.crh.interaction.util.ChainKeyStateTracker;
import top.fmutren.crh.network.ChainKeyStateMessage;

import static top.fmutren.crh.input.RightClick.ENCASE_MAPPING;
import static top.fmutren.crh.interaction.StateSwitch.isCasing;
import static top.fmutren.crh.interaction.StateSwitch.isCreateWrench;

public final class KeyDown {

    private static final ChainRender CHAIN_RENDER = new ChainRender();

    private static boolean lastSentChainKeyState;

    private KeyDown() {
    }

    public static void tickEnd(Player player) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.player != player) {
            return;
        }

        var level = player.level();
        syncChainKeyState(player);

        if (!Config.builtinChainAllowed()) {
            return;
        }

        if (ENCASE_MAPPING.consumeClick()) {
            displayKeyFeedback(player);
        }

        if (!ENCASE_MAPPING.isDown() || !level.isClientSide || !Config.enableView()) {
            return;
        }

        if (!(minecraft.hitResult instanceof BlockHitResult hit)) {
            return;
        }

        var pos = hit.getBlockPos();
        var mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        CHAIN_RENDER.getToRender(level, pos, mainHand);
    }

    public static void syncChainKeyState(Player player) {
        if (!Config.builtinChainAllowed()) {
            ChainKeyStateTracker.set(player, false);
            if (lastSentChainKeyState) {
                CrhServices.network().sendToServer(new ChainKeyStateMessage(false));
                lastSentChainKeyState = false;
            }
            return;
        }

        boolean chainKeyDown = ENCASE_MAPPING.isDown();
        ChainKeyStateTracker.set(player, chainKeyDown);

        if (chainKeyDown == lastSentChainKeyState) {
            return;
        }

        CrhServices.network().sendToServer(new ChainKeyStateMessage(chainKeyDown));
        lastSentChainKeyState = chainKeyDown;
    }

    private static void displayKeyFeedback(Player player) {
        String result = null;
        var mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        var offHand = player.getItemInHand(InteractionHand.OFF_HAND);

        if (isCreateWrench(mainHand) || isCreateWrench(offHand)) {
            result = "crh.message.altdownwithwrench";
        }

        if (isCasing(mainHand) || isCasing(offHand)) {
            result = "crh.message.altdownwithcasing";
        }

        if (result != null) {
            player.displayClientMessage(Component.translatable(result).withStyle(ChatFormatting.GREEN), true);
        }
    }

}
