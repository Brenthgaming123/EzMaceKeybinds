package com.ezelytramace;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;

/** Use an item from main hand, off hand, or the first matching hotbar slot (with swap back). */
abstract class ItemTrigger {
    private final HotbarSwap swap;
    private final Item item;

    ItemTrigger(HotbarSwap swap, Item item) {
        this.swap = swap;
        this.item = item;
    }

    public void press(Minecraft client) {
        LocalPlayer player = client.player;
        MultiPlayerGameMode gameMode = client.gameMode;
        if (player == null || gameMode == null || swap.busy()) return;

        if (player.getMainHandItem().is(item)) {
            gameMode.useItem(player, InteractionHand.MAIN_HAND);
            return;
        }
        if (player.getOffhandItem().is(item)) {
            gameMode.useItem(player, InteractionHand.OFF_HAND);
            return;
        }
        Inventory inv = player.getInventory();
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i).is(item)) {
                swap.start(client, i, HotbarSwap.Action.USE);
                return;
            }
        }
    }
}
