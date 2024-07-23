package com.chyzman.chowl.visage.item.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RenameMeLaterItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final Identifier modelId;

    public RenameMeLaterItemRenderer(Identifier modelId) {
        this.modelId = modelId;
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var model = MinecraftClient.getInstance().getBakedModelManager().getModel(modelId);
        if (model == null) return;

        matrices.push();
        matrices.translate(0.5F, 0.5F, 0.5F);
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, false, matrices, vertexConsumers, light, overlay, model);
        matrices.pop();
    }
}
