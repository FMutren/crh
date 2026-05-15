package top.fmutren.crh.input;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import top.fmutren.crh.interaction.util.ChainKeyStateTracker;
import top.fmutren.crh.interaction.util.PredicatesCreator;
import top.fmutren.crh.network.ModMessages;
import top.fmutren.crh.network.packet.ClickNetWorkHandle;

import static top.fmutren.crh.input.RightClick.ENCASE_MAPPING;

public final class KeyDown {

    private static boolean lastSentChainKeyState;

    private KeyDown() {
    }

    public static void tick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (Minecraft.getInstance().player != player) return;

        syncChainKeyState(player);

        if (ENCASE_MAPPING.get().consumeClick()) {
            String result = null;
            ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

            if (isCreateWrench(mainHand) || isCreateWrench(offHand)) {
                result = "crh.message.altdownwithwrench";
            }

            if (isCreateCasing(mainHand) || isCreateCasing(offHand)) {
                result = "crh.message.altdownwithcasing";
            }
            if (result == null) return;
            player.displayClientMessage(Component.translatable(result).withStyle(ChatFormatting.GREEN), true);
        }
    }

    public static void syncChainKeyState(Player player) {
        boolean chainKeyDown = ENCASE_MAPPING.get().isDown();
        ChainKeyStateTracker.set(player, chainKeyDown);

        if (chainKeyDown == lastSentChainKeyState) return;

        ModMessages.sendToServer(new ClickNetWorkHandle(chainKeyDown));
        lastSentChainKeyState = chainKeyDown;
    }

    private static boolean isCreateWrench(ItemStack stack) {
        return stack.getItem() instanceof WrenchItem;
    }

    private static boolean isCreateCasing(ItemStack stack) {
        return AllBlocks.COPPER_CASING.isIn(stack) || PredicatesCreator.isShaftCasing(stack);
    }
}
