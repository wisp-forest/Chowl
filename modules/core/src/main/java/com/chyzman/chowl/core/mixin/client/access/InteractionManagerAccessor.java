package com.chyzman.chowl.core.mixin.client.access;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerInteractionManager.class)
public interface InteractionManagerAccessor {
    @Invoker("syncSelectedSlot")
    void chowl$syncSelectedSlot();
}
