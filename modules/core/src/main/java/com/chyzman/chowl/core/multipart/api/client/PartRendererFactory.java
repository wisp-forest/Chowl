package com.chyzman.chowl.core.multipart.api.client;

import com.chyzman.chowl.core.multipart.api.Part;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.ItemRenderer;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface PartRendererFactory<T extends Part> {
    PartRenderer<T> create(PartRendererFactory.Context ctx);

    @Environment(EnvType.CLIENT)
    class Context {
        private final PartRenderDispatcher renderDispatcher;
        private final BlockRenderManager renderManager;
        private final ItemModelManager itemModelManager;
        private final ItemRenderer itemRenderer;
        private final EntityRenderDispatcher entityRenderDispatcher;
        private final LoadedEntityModels loadedEntityModels;
        private final TextRenderer textRenderer;

        public Context(
          PartRenderDispatcher renderDispatcher,
          BlockRenderManager renderManager,
          ItemModelManager itemModelManager,
          ItemRenderer itemRenderer,
          EntityRenderDispatcher entityRenderDispatcher,
          LoadedEntityModels layerRenderDispatcher,
          TextRenderer textRenderer
        ) {
            this.renderDispatcher = renderDispatcher;
            this.renderManager = renderManager;
            this.itemModelManager = itemModelManager;
            this.itemRenderer = itemRenderer;
            this.entityRenderDispatcher = entityRenderDispatcher;
            this.loadedEntityModels = layerRenderDispatcher;
            this.textRenderer = textRenderer;
        }

        public PartRenderDispatcher getRenderDispatcher() {
            return this.renderDispatcher;
        }

        public BlockRenderManager getRenderManager() {
            return this.renderManager;
        }

        public EntityRenderDispatcher getEntityRenderDispatcher() {
            return this.entityRenderDispatcher;
        }

        public ItemModelManager getItemModelManager() {
            return this.itemModelManager;
        }

        public ItemRenderer getItemRenderer() {
            return this.itemRenderer;
        }

        public LoadedEntityModels getLoadedEntityModels() {
            return this.loadedEntityModels;
        }

        public ModelPart getLayerModelPart(EntityModelLayer modelLayer) {
            return this.loadedEntityModels.getModelPart(modelLayer);
        }

        public TextRenderer getTextRenderer() {
            return this.textRenderer;
        }
    }
}
