package com.chyzman.chowl.core.client.render.block.entity;

import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import com.chyzman.chowl.core.multipart.api.Part;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

//TODO: get an oven (bake stuff)
public class MultipartBlockEntityRenderer implements BlockEntityRenderer<MultipartBlockEntity> {
    private final BlockEntityRendererFactory.Context context;

    public MultipartBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.context = context;
    }

    @Override
    public void render(MultipartBlockEntity entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrixStack.push();
        matrixStack.translate(0, 1.1, 0);
        matrixStack.scale(.01f, -.01f, 1);
        for (Part part : entity.getParts()) {
            context.getTextRenderer().draw(part.toString(), 0, 0, 0xFFFFFF, false, matrixStack.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
        }
        matrixStack.pop();
    }
}
