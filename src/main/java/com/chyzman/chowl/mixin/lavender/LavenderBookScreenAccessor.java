package com.chyzman.chowl.mixin.lavender;

import io.wispforest.lavender.client.LavenderBookScreen;
import io.wispforest.owo.ui.core.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LavenderBookScreen.class)
public interface LavenderBookScreenAccessor {
    @Invoker
    <C extends Component> C callTemplate(Class<C> expectedComponentClass, String name);
}
