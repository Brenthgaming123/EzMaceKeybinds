package com.ezelytramace;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;

public class ElytraArmorSwap {
    private final HotbarSwap swap;
    /** true = swap then switch back to the previous slot ("Revert"), false = plain "Swap". */
    private boolean revert = true;

    public ElytraArmorSwap(HotbarSwap swap) {
        this.swap = swap;
    }

    public void toggleMode(Minecraft client) {
        revert = !revert;
        Alert.modeChanged(client, revert ? "Revert" : "Swap", targetItemName(client));
    }

    private static String targetItemName(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) return "No item";
        boolean wearingElytra = isElytra(player.getItemBySlot(EquipmentSlot.CHEST));
        Inventory inv = player.getInventory();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && (wearingElytra ? isChestplate(stack) : isElytra(stack))) {
                return stack.getHoverName().getString();
            }
        }
        return wearingElytra ? "Chestplate not found" : "Elytra not found";
    }

    public void press(Minecraft client) {
        LocalPlayer player = client.player;
        MultiPlayerGameMode gameMode = client.gameMode;
        if (player == null || gameMode == null || swap.busy()) return;

        boolean wearingElytra = isElytra(player.getItemBySlot(EquipmentSlot.CHEST));
        Inventory inv = player.getInventory();

        if (matches(player.getMainHandItem(), wearingElytra)) {
            if (revert) {
                swap.start(client, inv.getSelectedSlot(), HotbarSwap.Action.USE_TWICE);
            } else {
                gameMode.useItem(player, InteractionHand.MAIN_HAND);
            }
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (matches(inv.getItem(i), wearingElytra)) {
                swap.start(client, i, revert ? HotbarSwap.Action.USE_TWICE : HotbarSwap.Action.USE);
                return;
            }
        }
    }

    private static boolean matches(ItemStack stack, boolean wearingElytra) {
        if (stack.isEmpty()) return false;
        return wearingElytra ? isChestplate(stack) : isElytra(stack);
    }

    private static boolean isElytra(ItemStack stack) {
        return stack.is(Items.ELYTRA);
    }

    private static boolean isChestplate(ItemStack stack) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null && equippable.slot() == EquipmentSlot.CHEST && !isElytra(stack);
    }
}
