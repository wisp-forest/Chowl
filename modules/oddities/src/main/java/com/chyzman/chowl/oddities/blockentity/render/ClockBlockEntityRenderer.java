package com.chyzman.chowl.oddities.blockentity.render;

import com.chyzman.chowl.oddities.block.ClockBlock;
import com.chyzman.chowl.oddities.blockentity.ClockBlockEntity;
import com.chyzman.chowl.oddities.mixin.access.WorldRendererAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);

        matrices.translate(0, 3.5f, 0);

        var scale = 1 / 64f;
        matrices.scale(scale, scale, scale);

        var client = MinecraftClient.getInstance();
        var world = entity.getWorld();

        var skyAngle = world.getSkyAngle(tickDelta);
        var moonPhase = world.getMoonPhase();
        var fog = BackgroundRenderer.applyFog(
                client.gameRenderer.getCamera(),
                BackgroundRenderer.FogType.FOG_SKY,
                new Vector4f(0, 0, 0, 0),
                10,
                false,
                tickDelta
        );

        var culling = GL11.glIsEnabled(GL11.GL_CULL_FACE);

        RenderSystem.disableCull();

        ((WorldRendererAccessor) client.worldRenderer).chowlOddities$getSkyRendering().renderCelestialBodies(
                matrices,
                (VertexConsumerProvider.Immediate) vertexConsumers,
                skyAngle,
                moonPhase,
                1,
                1,
                fog
        );

        if (culling) RenderSystem.enableCull();

        matrices.pop();
    }

    private boolean is24HourFormat() {
        String output = SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
        return !output.contains(" AM") && !output.contains(" PM");
    }
}
