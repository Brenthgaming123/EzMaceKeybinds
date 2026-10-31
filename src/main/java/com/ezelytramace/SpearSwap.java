package com.ezelytramace;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;

/**
 * Spear Swap – same mechanic as SpearSwapper (1.21.11):
 * click(attack) + setDown(true) + startAttack(), then 1 tick later setDown(false) + restore slot.
 */
public class SpearSwap {
    private static final int DELAY_TICKS = 1;
    private static Method startAttackMethod;
    private static boolean resolved;

    private int swapState = 0;
    private int targetSlot = -1;
    private int originalSlot = -1;
    private int ticksWaited = 0;
    private final HotbarSwap hotbar;

    public SpearSwap(HotbarSwap hotbar) {
        this.hotbar = hotbar;
    }

    public boolean busy() {
        return swapState != 0;
    }

    public void press(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null || client.gameMode == null || swapState != 0 || hotbar.busy()) {
            return;
        }
        Inventory inv = player.getInventory();
        int current = inv.getSelectedSlot();

        if (isSpear(player.getMainHandItem())) {
            doAttackBurst(client);
            return;
        }

        int spearSlot = findSpearSlot(inv);
        if (spearSlot < 0) {
            return;
        }
        targetSlot = spearSlot;
        originalSlot = current;
        swapState = 1;
        ticksWaited = 0;
    }

    public void tick(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) {
            reset();
            return;
        }
        Inventory inv = player.getInventory();

        if (swapState == 1) {
            inv.setSelectedSlot(targetSlot);
            doAttackBurst(client);
            swapState = 2;
            ticksWaited = 0;
            return;
        }

        if (swapState == 2) {
            ticksWaited++;
            if (ticksWaited >= DELAY_TICKS) {
                try {
                    client.options.keyAttack.setDown(false);
                } catch (Throwable ignored) {
                }
                if (originalSlot >= 0) {
                    inv.setSelectedSlot(originalSlot);
                }
                reset();
            }
        }
    }

    private static void doAttackBurst(Minecraft client) {
        KeyMapping attackKey = client.options.keyAttack;

        // 1) KeyMapping.click(bound attack key)
        try {
            InputConstants.Key bound = KeyMappingHelper.getBoundKeyOf(attackKey);
            if (bound != null) {
                KeyMapping.click(bound);
            }
        } catch (Throwable ignored) {
        }

        // 2) Hold attack down
        try {
            attackKey.setDown(true);
        } catch (Throwable ignored) {
        }

        // 3) Vanilla startAttack via reflection (same method SpearSwapper opens with access widener)
        try {
            Method m = resolveStartAttack();
            if (m != null) {
                m.invoke(client);
            }
        } catch (Throwable ignored) {
        }
    }

    private static Method resolveStartAttack() {
        if (resolved) {
            return startAttackMethod;
        }
        resolved = true;
        try {
            Method m = Minecraft.class.getDeclaredMethod("startAttack");
            m.setAccessible(true);
            startAttackMethod = m;
            return m;
        } catch (Throwable ignored) {
        }
        try {
            for (Method m : Minecraft.class.getDeclaredMethods()) {
                if (m.getParameterCount() == 0) {
                    String n = m.getName().toLowerCase();
                    if (n.contains("startattack") || n.equals("doattack")) {
                        m.setAccessible(true);
                        startAttackMethod = m;
                        return m;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private void reset() {
        swapState = 0;
        targetSlot = -1;
        originalSlot = -1;
        ticksWaited = 0;
    }

    static int findSpearSlot(Inventory inv) {
        for (int i = 0; i < 9; i++) {
            if (isSpear(inv.getItem(i))) {
                return i;
            }
        }
        return -1;
    }

    static boolean isSpear(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        try {
            if (stack.has(DataComponents.PIERCING_WEAPON)) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null) {
            String path = id.getPath();
            if (path.contains("spear") || path.contains("trident")) {
                return true;
            }
        }
        try {
            String name = stack.getHoverName().getString().toLowerCase();
            if (name.contains("spear") || name.contains("trident")) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }
}
