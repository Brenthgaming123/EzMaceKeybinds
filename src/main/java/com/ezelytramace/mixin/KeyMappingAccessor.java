package com.ezelytramace.mixin;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Access KeyMapping.clickCount so we can queue a real attack input. */
@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
    @Accessor("clickCount")
    int getClickCount();

    @Accessor("clickCount")
    void setClickCount(int value);
}
