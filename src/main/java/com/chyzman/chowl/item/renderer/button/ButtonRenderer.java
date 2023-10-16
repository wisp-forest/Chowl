package com.chyzman.chowl.item.renderer.button;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;

public interface ButtonRenderer {
    @Environment(EnvType.CLIENT)
    default void render(
            MinecraftClient client,
            DrawerFrameBlockEntity entity,
            BlockHitResult hitResult,
            VertexConsumerProvider vertexConsumers,
            MatrixStack matrices,
            int light,
            int overlay
    ) {
    }

    record StackButtonRenderer(ItemStack stack) implements ButtonRenderer {
        @Environment(EnvType.CLIENT)
        public void render(
                MinecraftClient client,
                DrawerFrameBlockEntity entity,
                BlockHitResult hitResult,
                VertexConsumerProvider vertexConsumers,
                MatrixStack matrices,
                int light,
                int overlay
        ) {
            matrices.scale(1, 1, 1 / 8f);
            client.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, overlay, client.getItemRenderer().getModels().getModel(stack));
            matrices.scale(1, 1, 8);
        }
    }
}