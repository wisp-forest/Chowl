package com.chyzman.chowl.core.attachables.api.client;

import com.chyzman.chowl.core.attachables.api.Attachable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface AttachableRendererFactory<T extends Attachable> {
    AttachableRenderer<T> create(Context ctx);

    @Environment(EnvType.CLIENT)
    class Context {
        private final AttachableRenderDispatcher renderDispatcher;
        private final ItemRenderer itemRenderer;
        private final ItemModelResolver itemModelManager;
        private final BlockRenderDispatcher blockRenderManager;
        private final BlockEntityRenderDispatcher blockEntityRenderManager;
        private final EntityRenderDispatcher entityRenderManager;
        private final Font textRenderer;

        public Context(
                AttachableRenderDispatcher renderDispatcher,
                ItemRenderer itemRenderer,
                ItemModelResolver itemModelManager,
                BlockRenderDispatcher blockRenderManager,
                EntityRenderDispatcher entityRenderManager,
                BlockEntityRenderDispatcher blockEntityRenderManager,
                Font textRenderer
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

        public ItemModelResolver itemModelManager() {
            return this.itemModelManager;
        }

        public BlockRenderDispatcher blockRenderManager() {
            return this.blockRenderManager;
        }

        public BlockEntityRenderDispatcher blockEntityRenderDispatcher() {
            return this.blockEntityRenderManager;
        }

        public EntityRenderDispatcher entityRenderDispatcher() {
            return this.entityRenderManager;
        }

        public Font textRenderer() {
            return this.textRenderer;
        }
    }
}
