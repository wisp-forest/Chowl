package com.chyzman.chowl.oddities.attachable.renderer;

import com.chyzman.chowl.core.attachables.api.client.AttachableRenderer;
import com.chyzman.chowl.oddities.attachable.PinAttachable;
import com.chyzman.chowl.oddities.registry.OdditiesItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class PinRenderer implements AttachableRenderer<PinAttachable> {
    @Override
    public void render(
            PinAttachable attachable,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        matrices.push();

        AttachableRenderer.drawDebugVector(matrices, vertexConsumers, attachable.pos(), attachable.rotation());
        AttachableRenderer.drawDebugBoundingBox(matrices, vertexConsumers, attachable.pos(), attachable.rotation(), attachable.getShape().shape().getBoundingBox());

        matrices.translate(attachable.pos());
        matrices.multiply(attachable.rotation());
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180));

        matrices.scale(1 / 6f, 1 / 6f, 1 / 6f);

        matrices.translate(0, 0.4, 0);

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                OdditiesItems.PIN.getDefaultStack(),
                ModelTransformationMode.FIXED,
                light,
                overlay,
                matrices,
                vertexConsumers,
                MinecraftClient.getInstance().world,
                0
        );

        matrices.pop();
    }

    @Override
    public void renderOutline(PinAttachable attachable, Camera camera, VertexConsumerProvider.Immediate vertexConsumers, MatrixStack matrices) {
        var camPos = camera.getPos();

        matrices.push();

        matrices.translate(camPos.multiply(-1));
        matrices.translate(attachable.pos());

        var highContrast = MinecraftClient.getInstance().options.getHighContrastBlockOutline().getValue();

        var shape = attachable.getShape();
        var voxelShape = shape.shape();

        matrices.multiply(shape.rotation());

        var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getSecondaryBlockOutline());
        if (highContrast) {
            VertexRendering.drawOutline(
                    matrices,
                    vertexConsumer,
                    voxelShape,
                    0,
                    0,
                    0,
                    -16777216
            );
        }

        vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        int color = highContrast ? Colors.CYAN : ColorHelper.withAlpha(102, Colors.BLACK);
        VertexRendering.drawOutline(
                matrices,
                vertexConsumer,
                voxelShape,
                0,
                0,
                0,
                color
        );
        vertexConsumers.drawCurrentLayer();

        matrices.pop();
    }
}
