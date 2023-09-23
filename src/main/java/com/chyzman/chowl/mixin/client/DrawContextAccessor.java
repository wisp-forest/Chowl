package com.chyzman.chowl.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {
    @Accessor
    void setMatrices(MatrixStack stack);
}
