package com.chyzman.chowl.oddities.attachable.renderer;

import com.chyzman.chowl.core.attachable.client.AttachableRenderer;
import com.chyzman.chowl.oddities.attachable.StringAttachable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StringRenderer implements AttachableRenderer<StringAttachable> {
    @Override
    public void render(
            StringAttachable attachable,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        matrices.push();

        AttachableRenderer.drawDebugVector(matrices, vertexConsumers, attachable.pos(), attachable.rotation());
        AttachableRenderer.drawDebugVector(matrices, vertexConsumers, attachable.endPos(), attachable.endRotation());
        var shape = attachable.getShape();
        AttachableRenderer.drawDebugBoundingBox(matrices, vertexConsumers, attachable.pos(), shape.rotation(), shape.shape().getBoundingBox());

        matrices.translate(attachable.pos());
        matrices.multiply(StringAttachable.getRotation(attachable.pos(), attachable.endPos()));

        matrices.scale(1 / 32f, 1 / 32f, 1 / 32f);

        var buffer = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(Identifier.ofVanilla("textures/entity/beacon_beam.png"), false));

        var length = (float) attachable.pos().distanceTo(attachable.endPos()) * 32f;

        renderStringCuboid(
                matrices.peek(),
                buffer,
                Colors.WHITE,
                -0.5f, 0, -0.5f,
                0.5f, length, 0.5f,
                light,
                overlay
        );

        matrices.pop();
    }

    @Override
    public void renderOutline(StringAttachable attachable, Camera camera, VertexConsumerProvider.Immediate vertexConsumers, MatrixStack matrices) {
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

    private static void renderStringCuboid(
            MatrixStack.Entry matrix,
            VertexConsumer vertices,
            int color,
            float startX, float startY, float startZ,
            float endX, float endY, float endZ,
            int light,
            int overlay
    ) {
        var size = endY - startY;
        var min = MathHelper.ceil(size);
        var max = MathHelper.fractionalPart(size);

        //North face
        renderStringVertex(matrix, vertices, color, startX, endY, startZ, 1, max, light, overlay);
        renderStringVertex(matrix, vertices, color, endX, endY, startZ, 0, max, light, overlay);
        renderStringVertex(matrix, vertices, color, endX, startY, startZ, 0, min, light, overlay);
        renderStringVertex(matrix, vertices, color, startX, startY, startZ, 1, min, light, overlay);

        //South face
        renderStringVertex(matrix, vertices, color, startX, startY, endZ, 0, min, light, overlay);
        renderStringVertex(matrix, vertices, color, endX, startY, endZ, 1, min, light, overlay);
        renderStringVertex(matrix, vertices, color, endX, endY, endZ, 1, max, light, overlay);
        renderStringVertex(matrix, vertices, color, startX, endY, endZ, 0, max, light, overlay);

        //East face
        renderStringVertex(matrix, vertices, color, startX, startY, startZ, 0, min, light, overlay);
        renderStringVertex(matrix, vertices, color, startX, startY, endZ, 1, min, light, overlay);
        renderStringVertex(matrix, vertices, color, startX, endY, endZ, 1, max, light, overlay);
        renderStringVertex(matrix, vertices, color, startX, endY, startZ, 0, max, light, overlay);

        //West face
        renderStringVertex(matrix, vertices, color, endX, endY, startZ, 1, max, light, overlay);
        renderStringVertex(matrix, vertices, color, endX, endY, endZ, 0, max, light, overlay);
        renderStringVertex(matrix, vertices, color, endX, startY, endZ, 0, min, light, overlay);
        renderStringVertex(matrix, vertices, color, endX, startY, startZ, 1, min, light, overlay);

        //Top face
        renderStringVertex(matrix, vertices, color, endX, endY, startZ, 1, 0, light, overlay);
        renderStringVertex(matrix, vertices, color, startX, endY, startZ, 0, 0, light, overlay);
        renderStringVertex(matrix, vertices, color, startX, endY, endZ, 0, 1, light, overlay);
        renderStringVertex(matrix, vertices, color, endX, endY, endZ, 1, 1, light, overlay);

        //Bottom face
        renderStringVertex(matrix, vertices, color, startX, startY, startZ, 1, 0, light, overlay);
        renderStringVertex(matrix, vertices, color, endX, startY, startZ, 0, 0, light, overlay);
        renderStringVertex(matrix, vertices, color, endX, startY, endZ, 0, 1, light, overlay);
        renderStringVertex(matrix, vertices, color, startX, startY, endZ, 1, 1, light, overlay);

    }

    private static void renderStringVertex(
            MatrixStack.Entry matrix,
            VertexConsumer vertices,
            int color,
            float x, float y, float z,
            float u, float v,
            int light,
            int overlay
    ) {
        vertices.vertex(matrix, x, y, z)
                .color(color)
                .texture(u, v)
                .overlay(overlay)
                .light(light)
                .normal(matrix, 0f, 1f, 0f);
    }
}
