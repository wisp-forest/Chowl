package com.chyzman.chowl.test.client.render;

import com.chyzman.chowl.core.multipart.api.client.PartRenderer;
import com.chyzman.chowl.core.multipart.api.client.PartRendererFactory;
import com.chyzman.chowl.test.multipart.TestPart;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;

public class TestPartRenderer extends PartRenderer<TestPart> {
    public TestPartRenderer(PartRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void renderBaked(TestPart part, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrixStack.push();
        matrixStack.scale(0.25f, 0.25f, 0.25f);
        matrixStack.translate(part.getStartPos().getX() / 4f, part.getStartPos().getY() / 4f, part.getStartPos().getZ() / 4f);

        Block block = Registries.BLOCK.get(Math.abs(part.getPos().hashCode() + part.getHolder().getParts().indexOf(part)) % Registries.BLOCK.size());

        if (block != Blocks.AIR) {
            context.getRenderManager().renderBlock(
              block.getDefaultState(),
              part.getHolder().getPos(),
              part.getWorld(),
              matrixStack,
              vertexConsumers.getBuffer(RenderLayer.getCutout()),
              false,
              part.getWorld().getRandom()
            );
        }

        matrixStack.pop();
    }

    @Override
    public void renderUnbaked(TestPart entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {}

    @Override
    public boolean shouldBake(TestPart entity) {
        return true;
    }
}
