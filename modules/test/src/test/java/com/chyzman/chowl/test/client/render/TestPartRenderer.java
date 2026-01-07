package com.chyzman.chowl.test.client.render;

import com.chyzman.chowl.core.multipart.api.client.render.PartRenderer;
import com.chyzman.chowl.core.multipart.api.client.PartRendererFactory;
import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import com.chyzman.chowl.test.multipart.TestPart;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.Vec3;
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
    public void submitForBaking(PartRenderState renderState, PoseStack matrices, SubmitNodeCollector queue) {
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
    public void submitForRendering(PartRenderState renderState, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraRenderState) {

    }

    @Override
    public boolean shouldBake(TestPart part, float tickProgress, Vec3 cameraPos) {
        return true;
    }
}
