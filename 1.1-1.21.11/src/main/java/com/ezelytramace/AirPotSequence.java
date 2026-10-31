package com.ezelytramace;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

/**
 * Throws splash potions (swiftness, healing, strength) and then a wind charge
 * in quick succession, then switches back to the original slot.
 */
public class AirPotSequence {
    private boolean active;
    private int elapsed;
    private int originalSlot = -1;
    private int windChargeSlot = -1;
    private int swiftnessSlot = -1;
    private int healingSlot = -1;
    private int strengthSlot = -1;

    public boolean busy() {
        return active;
    }

    public void start(Minecraft client) {
        LocalPlayer player = client.player;
        MultiPlayerGameMode gameMode = client.gameMode;
        if (player == null || gameMode == null || active) return;
        Inventory inv = player.getInventory();

        int wind = -1, swiftness = -1, healing = -1, strength = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getItem(i);
            if (wind == -1 && stack.is(Items.WIND_CHARGE)) wind = i;

            if (stack.is(Items.SPLASH_POTION)) {
                PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
                if (contents == null) continue;
                Holder<Potion> potion = contents.potion().orElse(null);

                if (swiftness == -1 && (Potions.SWIFTNESS.equals(potion)
                        || Potions.LONG_SWIFTNESS.equals(potion)
                        || Potions.STRONG_SWIFTNESS.equals(potion))) {
                    swiftness = i;
                }
                if (healing == -1 && (Potions.HEALING.equals(potion)
                        || Potions.STRONG_HEALING.equals(potion))) {
                    healing = i;
                }
                if (strength == -1 && (Potions.STRENGTH.equals(potion)
                        || Potions.LONG_STRENGTH.equals(potion)
                        || Potions.STRONG_STRENGTH.equals(potion))) {
                    strength = i;
                }
            }
        }
        if (wind == -1) return;

        originalSlot = inv.getSelectedSlot();
        windChargeSlot = wind;
        swiftnessSlot = swiftness;
        healingSlot = healing;
        strengthSlot = strength;
        elapsed = 0;
        active = true;

        if (swiftnessSlot != -1) {
            inv.setSelectedSlot(swiftnessSlot);
            gameMode.useItem(player, InteractionHand.MAIN_HAND);
        }
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
                if (healingSlot != -1) {
                    inv.setSelectedSlot(healingSlot);
                    gameMode.useItem(player, InteractionHand.MAIN_HAND);
                }
            }
            case 2 -> {
                if (strengthSlot != -1) {
                    inv.setSelectedSlot(strengthSlot);
                    gameMode.useItem(player, InteractionHand.MAIN_HAND);
                }
            }
            case 3 -> {
                inv.setSelectedSlot(windChargeSlot);
                gameMode.useItem(player, InteractionHand.MAIN_HAND);
            }
            case 4 -> {
                inv.setSelectedSlot(originalSlot);
                stop();
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
        windChargeSlot = -1;
        swiftnessSlot = -1;
        healingSlot = -1;
        strengthSlot = -1;
    }
}
