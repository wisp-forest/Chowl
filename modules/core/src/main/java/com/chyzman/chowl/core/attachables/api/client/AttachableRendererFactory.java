package com.chyzman.chowl.core.attachables.api.client;

import com.chyzman.chowl.core.attachables.api.Attachable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.item.ItemRenderer;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface AttachableRendererFactory<T extends Attachable> {
    AttachableRenderer<T> create(Context ctx);

    @Environment(EnvType.CLIENT)
    class Context {
        private final AttachableRenderDispatcher renderDispatcher;
        private final ItemRenderer itemRenderer;
        private final ItemModelManager itemModelManager;
        private final BlockRenderManager blockRenderManager;
        private final BlockEntityRenderManager blockEntityRenderManager;
        private final EntityRenderManager entityRenderManager;
        private final TextRenderer textRenderer;

        public Context(
                AttachableRenderDispatcher renderDispatcher,
                ItemRenderer itemRenderer,
                ItemModelManager itemModelManager,
                BlockRenderManager blockRenderManager,
                EntityRenderManager entityRenderManager,
                BlockEntityRenderManager blockEntityRenderManager,
                TextRenderer textRenderer
        ) {
            this.renderDispatcher = renderDispatcher;
            this.itemRenderer = itemRenderer;
            this.itemModelManager = itemModelManager;
            this.blockRenderManager = blockRenderManager;
            this.entityRenderManager = entityRenderManager;
            this.blockEntityRenderManager = blockEntityRenderManager;
            this.textRenderer = textRenderer;
        }

        public AttachableRenderDispatcher renderDispatcher() {
            return this.renderDispatcher;
        }

        public ItemRenderer itemRenderer() {
            return this.itemRenderer;
        }

        public ItemModelManager itemModelManager() {
            return this.itemModelManager;
        }

        public BlockRenderManager blockRenderManager() {
            return this.blockRenderManager;
        }

        public BlockEntityRenderManager blockEntityRenderDispatcher() {
            return this.blockEntityRenderManager;
        }

        public EntityRenderManager entityRenderDispatcher() {
            return this.entityRenderManager;
        }

        public TextRenderer textRenderer() {
            return this.textRenderer;
        }
    }
}
