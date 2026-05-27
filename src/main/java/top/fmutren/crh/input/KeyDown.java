package top.fmutren.crh.input;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.fmutren.crh.Config;
import top.fmutren.crh.interaction.util.ChainKeyStateTracker;
import top.fmutren.crh.interaction.util.PredicatesCreator;
import top.fmutren.crh.network.ModMessages;
import top.fmutren.crh.network.packet.ClickNetWorkHandle;

import static top.fmutren.crh.input.RightClick.ENCASE_MAPPING;

public final class KeyDown {

    private static boolean lastSentChainKeyState;

    private KeyDown() {}

    public static void syncChainKeyState(Player player) {
        if (!Config.builtinChainAllowed()) {
            ChainKeyStateTracker.set(player, false);
            if (lastSentChainKeyState) {
                ModMessages.sendToServer(new ClickNetWorkHandle(false));
                lastSentChainKeyState = false;
            }
            return;
        }

        boolean chainKeyDown = ENCASE_MAPPING.get().isDown();
        ChainKeyStateTracker.set(player, chainKeyDown);

        if (chainKeyDown == lastSentChainKeyState) return;

        ModMessages.sendToServer(new ClickNetWorkHandle(chainKeyDown));
        lastSentChainKeyState = chainKeyDown;
    }

    public static boolean isCreateWrench(ItemStack stack) {
        return stack.getItem() instanceof WrenchItem;
    }

    public static boolean isCreateCasing(ItemStack stack) {
        return AllBlocks.COPPER_CASING.isIn(stack) || PredicatesCreator.isCommonCasing(stack);
    }
}
