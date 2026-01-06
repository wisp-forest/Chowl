package com.chyzman.chowl.test.mixin;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.class)
public interface ItemRenderStateAccessor {
    @Accessor ItemStackRenderState.LayerRenderState[] getLayers();

    @Mixin(ItemStackRenderState.LayerRenderState.class)
    interface LayerRenderStateAccessor {
        @Accessor
        SpecialModelRenderer<Object> getSpecialRenderer();
    }
}
