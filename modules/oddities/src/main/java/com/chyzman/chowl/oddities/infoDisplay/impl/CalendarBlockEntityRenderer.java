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

public class CalendarBlockEntityRenderer extends InfoDisplayBlockEntityRenderer {

    @Override
    public void renderInfo(InfoDisplayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var client = MinecraftClient.getInstance();
        var textRenderer = MinecraftClient.getInstance().textRenderer;

        matrices.push();

        var text = String.valueOf(client.world.getTimeOfDay() / 24000L);

        var width = textRenderer.getWidth(text);

        textRenderer.draw(
                Text.literal(text),
                -width / 2f,
                -textRenderer.fontHeight / 2f,
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

//TODO: add the word days somewhere probably
//TODO: number format the number
