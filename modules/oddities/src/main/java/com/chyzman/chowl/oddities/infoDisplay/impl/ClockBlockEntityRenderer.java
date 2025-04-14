package com.chyzman.chowl.oddities.infoDisplay.impl;

import com.chyzman.chowl.oddities.infoDisplay.blockEntity.InfoDisplayBlockEntity;
import com.chyzman.chowl.oddities.infoDisplay.blockEntity.renderer.InfoDisplayBlockEntityRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;

public class ClockBlockEntityRenderer extends InfoDisplayBlockEntityRenderer {

    @Override
    public void renderInfo(InfoDisplayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var client = MinecraftClient.getInstance();
        var textRenderer = MinecraftClient.getInstance().textRenderer;

        matrices.push();

        var time = (client.world.getLunarTime() + 6000) % 24000;
        if (time < 0) time += 24000;

        var hour = MathHelper.floor(time / 1000d) % 12;
        if (hour == 0) hour = 12;

        var minute = MathHelper.floor(time / 1000d * 60) % 60;

        var timeText = String.format("%02d:%02d", hour, minute);

        var timewidth = textRenderer.getWidth(timeText);

        matrices.translate(-1.5, 0, 0);

        textRenderer.draw(
                Text.literal(timeText),
                -timewidth / 2f,
                -textRenderer.fontHeight / 2f,
                Colors.RED,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
        );

        matrices.translate(timewidth / 2f, 0, 0);

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
