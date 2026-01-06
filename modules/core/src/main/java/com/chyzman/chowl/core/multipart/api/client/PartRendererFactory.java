package com.chyzman.chowl.core.multipart.api.client;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderDispatcher;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderer;
import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.SpriteHolder;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface PartRendererFactory<T extends Part, S extends PartRenderState> {
    PartRenderer<T, S> create(PartRendererFactory.Context context);

    @Environment(EnvType.CLIENT)
    record Context(
      PartRenderDispatcher renderDispatcher,
      BlockRenderManager blockRenderDispatcher,
      BlockEntityRenderManager blockEntityRenderDispatcher,
      ItemModelManager itemModelResolver,
      ItemRenderer itemRenderer,
      EntityRenderManager entityRenderer,
      LoadedEntityModels loadedEntityModels,
      TextRenderer font,
      SpriteHolder materials,
      PlayerSkinCache playerSkinRenderCache
    ) {
        public ModelPart getLayerModelPart(EntityModelLayer modelLayer) {
            return this.loadedEntityModels.getModelPart(modelLayer);
        }
    }
}
