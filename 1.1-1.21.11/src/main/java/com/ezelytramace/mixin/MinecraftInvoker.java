package com.ezelytramace.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Access vanilla's private left-click attack. */
@Mixin(Minecraft.class)
public interface MinecraftInvoker {
    @Invoker("startAttack")
    boolean ezmace$startAttack();
}
