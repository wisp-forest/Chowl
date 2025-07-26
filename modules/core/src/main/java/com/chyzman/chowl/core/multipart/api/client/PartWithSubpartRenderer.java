package com.chyzman.chowl.core.multipart.api.client;

import com.chyzman.chowl.core.multipart.api.Part;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public abstract class PartWithSubpartRenderer<T extends Part> extends PartRenderer<T> {
    protected final Map<Class<? extends Part>, SubPartRenderer> renderers = new HashMap<>();

    protected PartWithSubpartRenderer(PartRendererFactory.Context context) {
        super(context);
    }

    protected <S extends Part> void addRenderer(Class<S> subPartClass, SubPartRenderer<S> renderer) {
        renderers.put(subPartClass, renderer);
    }

    @SuppressWarnings("unchecked")
    protected void renderSubPartsBaked(T parent, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        for (Part subPart : parent.getSubParts()) {
            SubPartRenderer renderer = renderers.get(subPart.getClass());
            if (renderer != null) {
                renderer.renderBaked(subPart, parent, matrices, vertexConsumers, light, overlay);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void renderSubPartsUnbaked(T parent, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        for (Part subPart : parent.getSubParts()) {
            SubPartRenderer renderer = renderers.get(subPart.getClass());
            if (renderer != null) {
                renderer.renderUnbaked(subPart, parent, tickDelta, matrices, vertexConsumers, light, overlay);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean shouldBake(T parent) {
        for (Part subPart : parent.getSubParts()) {
            SubPartRenderer renderer = renderers.get(subPart.getClass());
            if (renderer != null && renderer.shouldBake(subPart, parent)) {
                return true;
            }
        }

        return shouldBakePart(parent);
    }

    public abstract boolean shouldBakePart(T part);

    public abstract class SubPartRenderer<S extends Part> {
        /**
         * Render vertices to be baked into the render region. This method will be called every time the render region is rebuilt - so
         * you should only render vertices that don't move here. You can call {@link Manager#markForRebuild(BlockPos)} to
         * cause the render region to be rebuilt, but do not call this too frequently as it will affect performance.
         * You must use the provided VertexConsumerProvider and MatrixStack to render your vertices - any use of Tessellator
         * or RenderSystem here will not work. If you need custom rendering settings, you can use a custom RenderLayer.
         */
        public abstract void renderBaked(S part, T parent, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);

        /**
         * Render vertices immediately. This works exactly the same way as a normal BER render method, and can be used for dynamic
         * rendering that changes every frame. In this method you can also check for render invalidation and call {@link Manager#markForRebuild(BlockPos)}
         * as appropriate.
         */
        public abstract void renderUnbaked(S part, T parent, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);

        public abstract boolean shouldBake(S part, T parent);
    }
}
