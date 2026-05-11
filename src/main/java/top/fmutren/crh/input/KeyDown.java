package top.fmutren.crh.input;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.fmutren.crh.interaction.util.ChainKeyStateTracker;
import top.fmutren.crh.interaction.util.PredicatesCreator;
import top.fmutren.crh.network.ModMessages;

import static top.fmutren.crh.input.RightClick.ENCASE_MAPPING;

public final class KeyDown {

    private static boolean lastSentChainKeyState;

    private KeyDown() {
    }

    public static void tick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (Minecraft.getInstance().player != player) return;

        syncChainKeyState(player);

        if (ENCASE_MAPPING.get().consumeClick()) {
            player.displayClientMessage(messageForHeldItem(player), true);
        }
    }

    public static void syncChainKeyState(Player player) {
        boolean chainKeyDown = ENCASE_MAPPING.get().isDown();
        ChainKeyStateTracker.set(player, chainKeyDown);

        if (chainKeyDown == lastSentChainKeyState) return;

        PacketDistributor.sendToServer(new ModMessages.ChainKeyStatePayload(chainKeyDown));
        lastSentChainKeyState = chainKeyDown;
    }

    private static Component messageForHeldItem(Player player) {
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

        if (isCreateWrench(mainHand) || isCreateWrench(offHand)) {
            return Component.translatable("crh.message.altdownwithwrench")
                    .withStyle(ChatFormatting.GREEN);
        }

        if (isCreateCasing(mainHand) || isCreateCasing(offHand)) {
            return Component.translatable("crh.message.altdownwithcasing")
                    .withStyle(ChatFormatting.GREEN);
        }

        return Component.translatable("crh.message.chainkey")
                .withStyle(ChatFormatting.GREEN);
    }

    private static boolean isCreateWrench(ItemStack stack) {
        return stack.getItem() instanceof WrenchItem;
    }

    private static boolean isCreateCasing(ItemStack stack) {
        return AllBlocks.COPPER_CASING.isIn(stack) || PredicatesCreator.isShaftCasing(stack);
    }

}
