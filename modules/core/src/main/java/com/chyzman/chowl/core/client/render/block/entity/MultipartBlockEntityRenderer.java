package com.chyzman.chowl.core.client.render.block.entity;

import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.pond.MinecraftClientDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class MultipartBlockEntityRenderer implements BlockEntityRenderer<MultipartBlockEntity> {
    public MultipartBlockEntityRenderer(BlockEntityRendererFactory.Context ignored) {}

    @Override
    public void render(MultipartBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        for (Part part : entity.getParts()) {
            matrices.push();
            ((MinecraftClientDuck) MinecraftClient.getInstance()).chowl$getPartRenderDispatcher().render(part, tickDelta, matrices, vertexConsumers);
            matrices.pop();
        }
        matrices.pop();
    }
}
