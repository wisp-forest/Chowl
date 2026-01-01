package com.chyzman.chowl.test.mixin;

import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderState.class)
public interface ItemRenderStateAccessor {
    @Accessor ItemRenderState.LayerRenderState[] getLayers();

    @Mixin(ItemRenderState.LayerRenderState.class)
    interface LayerRenderStateAccessor {
        @Accessor BakedModel getModel();
    }
}
