package com.ezelytramace;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class EzElytraMace implements ClientModInitializer {
    public static final String MOD_ID = "ezmacekeybinds";
    public static final int UNBOUND = -1;

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));

    @Override
    public void onInitializeClient() {
        // Order in Controls menu follows registration order; spear is last (bottom)
        KeyMapping fireworkKey = key("firework_trigger");
        KeyMapping armorSwapKey = key("elytra_armor_swap");
        KeyMapping modeToggleKey = key("elytra_mode_toggle");
        KeyMapping windChargeKey = key("wind_charge_trigger");
        KeyMapping pearlCatchKey = key("pearl_catch_sequence");
        KeyMapping airPotKey = key("air_pot_sequence");
        KeyMapping throwPearlKey = key("throw_pearl");
        KeyMapping spearSwapKey = key("spear_swap"); // bottom

        HotbarSwap swap = new HotbarSwap();
        FireworkTrigger firework = new FireworkTrigger(swap);
        ElytraArmorSwap armorSwap = new ElytraArmorSwap(swap);
        WindChargeTrigger windCharge = new WindChargeTrigger(swap);
        PearlCatchSequence pearlCatch = new PearlCatchSequence();
        AirPotSequence airPot = new AirPotSequence();
        ThrowPearlSequence throwPearl = new ThrowPearlSequence();
        SpearSwap spearSwap = new SpearSwap(swap);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            swap.tick(client);
            pearlCatch.tick(client);
            airPot.tick(client);
            throwPearl.tick(client);
            spearSwap.tick(client);

            boolean busy = pearlCatch.busy() || airPot.busy() || throwPearl.busy()
                    || swap.busy() || spearSwap.busy();

            if (airPotKey.consumeClick() && !busy) {
                airPot.start(client);
            } else if (throwPearlKey.consumeClick() && !busy) {
                throwPearl.start(client);
            } else if (pearlCatchKey.consumeClick() && !busy) {
                pearlCatch.start(client, swap);
            }

            if (!pearlCatch.busy() && !airPot.busy() && !throwPearl.busy() && !spearSwap.busy()) {
                while (fireworkKey.consumeClick()) firework.press(client);
                while (armorSwapKey.consumeClick()) armorSwap.press(client);
                while (modeToggleKey.consumeClick()) armorSwap.toggleMode(client);
                while (windChargeKey.consumeClick()) windCharge.press(client);
                while (spearSwapKey.consumeClick()) spearSwap.press(client);
            }
        });
    }

    private static KeyMapping key(String name) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key." + MOD_ID + "." + name,
                InputConstants.Type.KEYSYM,
                UNBOUND,
                CATEGORY));
    }
}
