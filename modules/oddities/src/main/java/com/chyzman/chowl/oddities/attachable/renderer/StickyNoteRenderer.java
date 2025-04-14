package com.chyzman.chowl.oddities.attachable.renderer;

import com.chyzman.chowl.core.attachables.api.client.AttachableRenderer;
import com.chyzman.chowl.core.attachables.api.client.AttachableRendererFactory;
import com.chyzman.chowl.oddities.attachable.StickyNoteAttachable;
import com.chyzman.chowl.oddities.registry.OdditiesItems;
import io.wispforest.owo.util.Wisdom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class StickyNoteRenderer implements AttachableRenderer<StickyNoteAttachable> {
    private final TextRenderer textRenderer;

    public StickyNoteRenderer(AttachableRendererFactory.Context context) {
        this.textRenderer = context.textRenderer();
    }

    @Override
    public void render(
            StickyNoteAttachable attachable,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        matrices.push();

        AttachableRenderer.drawDebugVector(matrices, vertexConsumers, attachable.pos(), attachable.rotation());
        var shape = attachable.getShape();
        AttachableRenderer.drawDebugBoundingBox(matrices, vertexConsumers, attachable.pos(), shape.rotation(), shape.shape().getBoundingBox());

        matrices.translate(attachable.pos());
        matrices.multiply(attachable.rotation());
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));

        matrices.scale(1 / 4f, 1 / 4f, 1 / 4f);

        matrices.translate(0, -0.5, -1 / 16f);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-5));
        matrices.translate(0, 0.1, 0);

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                OdditiesItems.STICKY_NOTE.getDefaultStack(),
                ModelTransformationMode.FIXED,
                light,
                overlay,
                matrices,
                vertexConsumers,
                MinecraftClient.getInstance().world,
                0
        );
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));

        matrices.translate(-0.5, 0.5, 0);

        matrices.scale(1 / 16f, 1 / 16f, 1 / 16f);

        matrices.translate(0.5, 1, 0.51);

        matrices.scale(1.5f, 1.5f, 1.5f);

        var fontSpacing = textRenderer.fontHeight + 3f;

        matrices.scale(1 / fontSpacing, -1 / fontSpacing, 1 / fontSpacing);

        matrices.translate(0, fontSpacing * 2, 0);

        var wrapped = textRenderer.wrapLines(Text.literal(Wisdom.ALL_THE_WISDOM.get(new Random(attachable.pos().hashCode()).nextInt(Wisdom.ALL_THE_WISDOM.size()))), 120);

        wrapped = wrapped.subList(0, Math.min(9, wrapped.size()));

        var offset = 0;
        for (var orderedText : wrapped) {
            textRenderer.draw(
                    orderedText,
                    0,
                    offset,
                    0,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL,
                    0,
                    light
            );
            offset += fontSpacing;
        }

        matrices.pop();
    }

    @Override
    public void renderOutline(StickyNoteAttachable attachable, Camera camera, VertexConsumerProvider.Immediate vertexConsumers, MatrixStack matrices) {
        var camPos = camera.getPos();

        matrices.push();

        matrices.translate(camPos.multiply(-1));
        matrices.translate(attachable.pos());

//        matrices.multiply(attachable.rotation());
//        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
//
//        matrices.scale(1 / 4f, 1 / 4f, 1 / 4f);
//
//        matrices.translate(0, -0.5, -1 / 16f);
//        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-5));
//        matrices.translate(0, 0.1, 0);

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
