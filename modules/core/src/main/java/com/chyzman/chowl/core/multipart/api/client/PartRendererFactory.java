package com.chyzman.chowl.core.multipart.api.client;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderDispatcher;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderer;
import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.resources.model.MaterialSet;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface PartRendererFactory<T extends Part, S extends PartRenderState> {
    PartRenderer<T, S> create(PartRendererFactory.Context context);

    @Environment(EnvType.CLIENT)
    record Context(
      PartRenderDispatcher renderDispatcher,
      BlockRenderDispatcher blockRenderDispatcher,
      BlockEntityRenderDispatcher blockEntityRenderDispatcher,
      ItemModelResolver itemModelResolver,
      ItemRenderer itemRenderer,
      EntityRenderDispatcher entityRenderer,
      EntityModelSet loadedEntityModels,
      Font font,
      MaterialSet materials,
      PlayerSkinRenderCache playerSkinRenderCache
    ) {
        public ModelPart getLayerModelPart(ModelLayerLocation modelLayer) {
            return this.loadedEntityModels.bakeLayer(modelLayer);
        }
    }
}
