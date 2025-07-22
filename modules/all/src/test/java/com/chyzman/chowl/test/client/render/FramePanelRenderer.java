package com.chyzman.chowl.test.client.render;

import com.chyzman.chowl.core.multipart.api.client.PartRenderer;
import com.chyzman.chowl.core.multipart.api.client.PartRendererFactory;
import com.chyzman.chowl.core.util.VoxelShapeHelper;
import com.chyzman.chowl.test.multipart.FramePanel;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class FramePanelRenderer extends PartRenderer<FramePanel> {
    public FramePanelRenderer(PartRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void renderBaked(FramePanel part, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrixStack.push();

        matrixStack.pop();
    }

    @Override
    public void renderUnbaked(FramePanel part, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrixStack.push();

        VoxelShape shape = part.getOutlineShape(null, null, null, null);

        //DebugRenderer.drawBox(matrices, vertexConsumers, pos, 0.02F, marker.getRed(), marker.getBlue(), marker.getGreen(), marker.getAlpha() * 0.75F);
        Box box = shape.getBoundingBox();
        DebugRenderer.drawBox(matrixStack, vertexConsumers, box, 1, 0, 0, 0.5f);

        matrixStack.pop();
    }

    @Override
    public boolean shouldBake(FramePanel entity) {
        return true;
    }
}
