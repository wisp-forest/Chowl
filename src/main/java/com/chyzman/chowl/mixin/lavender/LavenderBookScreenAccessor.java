package com.chyzman.chowl.mixin.lavender;

import io.wispforest.lavender.client.LavenderBookScreen;
import io.wispforest.owo.ui.core.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = LavenderBookScreen.class, remap = false)
public interface LavenderBookScreenAccessor {
    @Invoker
    <C extends Component> C callTemplate(Class<C> expectedComponentClass, String name);
}
