package top.fmutren.crh.event;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.TickEvent;
import top.fmutren.crh.Config;
import top.fmutren.crh.interaction.ChainRender;

import static top.fmutren.crh.input.KeyDown.*;
import static top.fmutren.crh.input.RightClick.ENCASE_MAPPING;

public class PlayerEventCreator {
    public static void tick(TickEvent.PlayerTickEvent event) {

        if(Config.ftbUltimineCompatActive()) return;

        Player player = event.player;
        if (Minecraft.getInstance().player != player) return;
        Level level = player.level();

        syncChainKeyState(player);

        if (ENCASE_MAPPING.get().consumeClick()) {
            String result = null;
            ChatFormatting color = null;
            ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

            if (isCreateWrench(mainHand) || isCreateWrench(offHand)) {
                result = "crh.message.altdownwithwrench";
                color = ChatFormatting.RED;
            }

            if (isCreateCasing(mainHand) || isCreateCasing(offHand)) {
                result = "crh.message.altdownwithcasing";
                color = ChatFormatting.GREEN;
            }
            if (result == null) return;
            player.displayClientMessage(Component.translatable(result).withStyle(color), true);
        }

        if(ENCASE_MAPPING.get().isDown()) {
            if(!level.isClientSide) return;
            if(!(Minecraft.getInstance().hitResult instanceof BlockHitResult hit)) return;
            BlockPos pos = hit.getBlockPos();
            ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(Config.enableView()) {
                ChainRender instance = new ChainRender();
                instance.getToRender(level, pos, mainHand);
            }
        }
    }
}
