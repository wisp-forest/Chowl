package com.chyzman.chowl.oddities.blockentity.render;

import com.chyzman.chowl.oddities.block.ClipboardBlock;
import com.chyzman.chowl.oddities.blockentity.ClipboardBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class ClipboardBlockEntityRenderer implements BlockEntityRenderer<ClipboardBlockEntity> {
    private final TextRenderer textRenderer;

    public ClipboardBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.textRenderer = context.getTextRenderer();
    }

    @Override
    public void render(
            ClipboardBlockEntity entity,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        matrices.push();

        var orientation = entity.getCachedState().get(ClipboardBlock.ORIENTATION);
        var facing = orientation.getFacing();
        var rotation = orientation.getRotation();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(facing.getRotationQuaternion());
        matrices.translate(0, 0.5, 0);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
        if (facing.getAxis() == Direction.Axis.Y) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.getPositiveHorizontalDegrees()));
            if (facing == Direction.UP && rotation.getAxis() == Direction.Axis.Z) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
            }
        }
        matrices.translate(-0.5, 0.5, -1);
        matrices.scale(1 / 16f, 1 / 16f, 1 / 16f);

        matrices.translate(4, 0, 1.255);

        var fontSpacing = textRenderer.fontHeight + 3f;

        matrices.scale(1/fontSpacing, -1/fontSpacing, 1/fontSpacing);

        matrices.translate(0, fontSpacing * 2, 0);

//        var wrapped = textRenderer.wrapLines(entity.content, 110);

        var offset = 2;
        for (var line : entity.contents) {
            textRenderer.draw(
                    line.text,
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
}
