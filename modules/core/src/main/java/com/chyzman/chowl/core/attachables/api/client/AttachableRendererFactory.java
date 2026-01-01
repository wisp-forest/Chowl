package com.chyzman.chowl.core.attachables.api.client;

import com.chyzman.chowl.core.attachables.api.Attachable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface AttachableRendererFactory<T extends Attachable> {
    AttachableRenderer<T> create(Context ctx);

    @Environment(EnvType.CLIENT)
    class Context {
        private final AttachableRenderDispatcher renderDispatcher;
        private final ItemRenderer itemRenderer;
        private final BakedModelManager bakedModelManager;
        private final BlockRenderManager blockRenderManager;
        private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
        private final EntityRenderDispatcher entityRenderDispatcher;
        private final TextRenderer textRenderer;

        public Context(
                AttachableRenderDispatcher renderDispatcher,
                ItemRenderer itemRenderer,
                BakedModelManager bakedModelManager,
                BlockRenderManager blockRenderManager,
                EntityRenderDispatcher entityRenderDispatcher,
                BlockEntityRenderDispatcher blockEntityRenderDispatcher,
                TextRenderer textRenderer
        ) {
            this.renderDispatcher = renderDispatcher;
            this.itemRenderer = itemRenderer;
            this.bakedModelManager = bakedModelManager;
            this.blockRenderManager = blockRenderManager;
            this.entityRenderDispatcher = entityRenderDispatcher;
            this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
            this.textRenderer = textRenderer;
        }

        public AttachableRenderDispatcher renderDispatcher() {
            return this.renderDispatcher;
        }

        public ItemRenderer itemRenderer() {
            return this.itemRenderer;
        }

        public BakedModelManager itemModelManager() {
            return this.bakedModelManager;
        }

        public BlockRenderManager blockRenderManager() {
            return this.blockRenderManager;
        }

        public BlockEntityRenderDispatcher blockEntityRenderDispatcher() {
            return this.blockEntityRenderDispatcher;
        }

        public EntityRenderDispatcher entityRenderDispatcher() {
            return this.entityRenderDispatcher;
        }

        public TextRenderer textRenderer() {
            return this.textRenderer;
        }
    }
}
