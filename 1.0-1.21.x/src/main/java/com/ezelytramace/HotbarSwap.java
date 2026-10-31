package com.ezelytramace;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;

/**
 * Selects a hotbar slot, uses the item on the next tick(s), then restores the original slot.
 * Spear Swap is handled separately by SpearSwap (does not use this class for ATTACK).
 */
public class HotbarSwap {
    public enum Action { USE, USE_TWICE }

    private int returnSlot = -1;
    private int elapsed;
    private Action action = Action.USE;
    private int targetSlot = -1;

    public boolean busy() {
        return returnSlot != -1;
    }

    public void start(Minecraft client, int slot, Action action) {
        LocalPlayer player = client.player;
        if (player == null || busy()) {
            return;
        }
        Inventory inv = player.getInventory();
        this.returnSlot = inv.getSelectedSlot();
        this.elapsed = 0;
        this.action = action;
        this.targetSlot = slot;
        inv.setSelectedSlot(slot);
    }

    public void tick(Minecraft client) {
        if (!busy()) {
            return;
        }
        LocalPlayer player = client.player;
        MultiPlayerGameMode gameMode = client.gameMode;
        if (player == null || gameMode == null) {
            reset();
            return;
        }

        elapsed++;

        if (elapsed == 1) {
            reselectTarget(player);
            gameMode.useItem(player, InteractionHand.MAIN_HAND);
            return;
        }

        if (action == Action.USE_TWICE) {
            reselectTarget(player);
            gameMode.useItem(player, InteractionHand.MAIN_HAND);
        }

        if (returnSlot >= 0) {
            player.getInventory().setSelectedSlot(returnSlot);
        }
        reset();
    }

    private void reset() {
        returnSlot = -1;
        targetSlot = -1;
        elapsed = 0;
    }

    private void reselectTarget(LocalPlayer player) {
        if (targetSlot >= 0) {
            Inventory inv = player.getInventory();
            if (inv.getSelectedSlot() != targetSlot) {
                inv.setSelectedSlot(targetSlot);
            }
        }
    }
}
