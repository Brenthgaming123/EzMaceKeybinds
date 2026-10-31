package com.ezelytramace;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;

/** Select an ender pearl, throw it, switch back. */
public class ThrowPearlSequence {
    private boolean active;
    private int elapsed;
    private int originalSlot = -1;
    private int pearlSlot = -1;

    public boolean busy() {
        return active;
    }

    public void start(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null || client.gameMode == null || active) return;
        Inventory inv = player.getInventory();

        int pearl = -1;
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i).is(Items.ENDER_PEARL)) {
                pearl = i;
                break;
            }
        }
        if (pearl == -1) return;

        originalSlot = inv.getSelectedSlot();
        pearlSlot = pearl;
        elapsed = 0;
        active = true;
        inv.setSelectedSlot(pearlSlot);
    }

    public void tick(Minecraft client) {
        if (!active) return;
        LocalPlayer player = client.player;
        MultiPlayerGameMode gameMode = client.gameMode;
        if (player == null || gameMode == null) {
            stop();
            return;
        }
        Inventory inv = player.getInventory();
        elapsed++;
        switch (elapsed) {
            case 1 -> {
                inv.setSelectedSlot(pearlSlot);
                gameMode.useItem(player, InteractionHand.MAIN_HAND);
            }
            default -> {
                inv.setSelectedSlot(originalSlot);
                stop();
            }
        }
    }

    private void stop() {
        active = false;
        elapsed = 0;
        originalSlot = -1;
        pearlSlot = -1;
    }
}
