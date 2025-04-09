package com.chyzman.chowl.oddities.blockentity.render;

import com.chyzman.chowl.oddities.block.ClockBlock;
import com.chyzman.chowl.oddities.blockentity.ClockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ClockBlockEntityRenderer implements BlockEntityRenderer<ClockBlockEntity> {
    private final TextRenderer textRenderer;

    public ClockBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.textRenderer = context.getTextRenderer();
    }

    @Override
    public void render(
            ClockBlockEntity entity,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        matrices.push();

        var facing = entity.getCachedState().get(ClockBlock.FACING);

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(facing.getRotationQuaternion());
        matrices.translate(0, 0.5, 0);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));

        matrices.translate(0, 0, -0.5);
        matrices.scale(1 / 16f, 1 / 16f, 1 / 16f);
        matrices.translate(0, -6.75, 2.01);

        matrices.scale(3f / textRenderer.fontHeight, -3f / textRenderer.fontHeight, 3f / textRenderer.fontHeight);

        var time = (entity.getWorld().getLunarTime() + 6000) % 24000;
        if (time < 0) time += 24000;

        var hour = MathHelper.floor(time / 1000d) % 12;
        if (hour == 0) hour = 12;

        var minute = MathHelper.floor(time / 1000d * 60) % 60;

        var timeText = String.format("%02d:%02d", hour, minute);

        var timewidth = textRenderer.getWidth(timeText);

//        textRenderer.draw(
//                Text.literal(String.valueOf(time)),
//                -timewidth / 2f,
//                -textRenderer.fontHeight * 2f,
//                Colors.RED,
//                false,
//                matrices.peek().getPositionMatrix(),
//                vertexConsumers,
//                TextRenderer.TextLayerType.NORMAL,
//                0,
//                LightmapTextureManager.MAX_LIGHT_COORDINATE
//        );

        textRenderer.draw(
                Text.literal(timeText),
                -timewidth / 2f,
                -textRenderer.fontHeight,
                Colors.RED,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
        );

        matrices.translate(timewidth / 2f, -textRenderer.fontHeight / 2f, 0);

        matrices.scale(1 / 3f, 1 / 3f, 1 / 3f);

        var meridiem = time >= 12000 ? "PM" : "AM";

        textRenderer.draw(
                Text.literal(meridiem),
                0,
                0.5f,
                Colors.RED,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
        );

        matrices.pop();
    }
}
