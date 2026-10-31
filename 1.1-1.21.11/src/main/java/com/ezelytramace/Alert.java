package com.ezelytramace;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class Alert {
    private Alert() {}

    public static void modeChanged(Minecraft client, String mode, String itemName) {
        Component message = Component.literal("Successfully changed to ").withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(Component.literal(mode).withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD))
                .append(Component.literal(" \u2014 ").withStyle(ChatFormatting.LIGHT_PURPLE))
                .append(Component.literal(itemName).withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
        send(client, message);
    }

    public static void send(Minecraft client, Component message) {
        if (client.player == null) return;
        client.player.displayClientMessage(message, true); // action bar
    }
}
