package com.chyzman.chowl.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class DrawerFrameBlockEntityRenderer implements BlockEntityRenderer<DrawerFrameBlockEntity> {
    public DrawerFrameBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(DrawerFrameBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var client = MinecraftClient.getInstance();
        var world = entity.getWorld();

        renderSelectedButton(client, entity, vertexConsumers, matrices);

        renderPanels(entity, client, world, tickDelta, matrices, vertexConsumers, light, overlay);
    }

    public void renderSelectedButton(MinecraftClient client, DrawerFrameBlockEntity entity, VertexConsumerProvider vertexConsumers, MatrixStack matrices) {
        if (!(client.crosshairTarget instanceof BlockHitResult hitResult)) return;
        if (!hitResult.getBlockPos().equals(entity.getPos())) return;
        if (!(entity.getCachedState().getBlock() instanceof BlockButtonProvider buttonProvider)) return;

        var button = buttonProvider.findButton(entity.getWorld(), entity.getCachedState(), hitResult, client.player);

        if (button == null) return;

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(hitResult.getSide().getRotationQuaternion());
        matrices.translate(0, 0.5, 0);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));

        matrices.translate(-0.5, 0.5, 0);
        matrices.scale(button.maxX() - button.minX(), button.maxY() - button.minY(), 1);
        matrices.translate(0.5, -0.5, 0);
//        matrices.scale(-1, -1, -1);

//        var drawCtx = OwoUIDrawContext.of(new DrawContext(client, client.getBufferBuilders().getEntityVertexConsumers()));
//        ((DrawContextAccessor) drawCtx).setMatrices(matrices);
//        drawCtx.drawPanel(0, 0, 1, 1, false);

        var stack = Items.BARRIER.getDefaultStack();
        client.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, client.getItemRenderer().getModels().getModel(stack));

        matrices.pop();
    }

    public static void renderPanels(DrawerFrameBlockEntity entity, MinecraftClient client, World world, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        for (int i = 0; i < entity.stacks.length; i++) {
            var stack = entity.stacks[i];
            if (!stack.isEmpty()) {
                matrices.push();
                matrices.translate(0.5, 0.5, 0.5);
                matrices.multiply(Direction.byId(i).getRotationQuaternion());
                matrices.translate(0, 0.5, 0);
                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
                matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));
                var light_level = world != null ? WorldRenderer.getLightmapCoordinates(world, entity.getPos().offset(Direction.byId(i))) : LightmapTextureManager.MAX_LIGHT_COORDINATE;
                client.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light_level, overlay, client.getItemRenderer().getModels().getModel(stack));
                matrices.pop();
            }
        }
    }
}