package com.chyzman.chowl.test.client.render;

import com.chyzman.chowl.core.multipart.api.client.render.PartRenderer;
import com.chyzman.chowl.core.multipart.api.client.PartRendererFactory;
import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import com.chyzman.chowl.test.multipart.TestPart;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class TestPartRenderer implements PartRenderer<TestPart, PartRenderState> {
    private final PartRendererFactory.Context context;

    public TestPartRenderer(PartRendererFactory.Context context) {
        this.context = context;
    }

    @Override
    public @NotNull PartRenderState createRenderState() {
        return new PartRenderState();
    }

    @Override
    public void renderBaked(PartRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue) {
        /*matrixStack.push();
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

        matrixStack.pop();*/
    }

    @Override
    public void renderUnbaked(PartRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {

    }

    @Override
    public boolean shouldBake(TestPart part, float tickProgress, Vec3d cameraPos) {
        return true;
    }
}
