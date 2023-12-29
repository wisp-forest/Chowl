package com.chyzman.chowl.block.button;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;

import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

public interface ButtonRenderer {
    static ButtonRenderer empty() {
        return (client, entity, hitResult, vertexConsumers, matrices, light, overlay) -> {
        };
    }

    static ButtonRenderer stack(ItemStack stack) {
        return (client, entity, hitResult, vertexConsumers, matrices, light, overlay) -> {
            matrices.scale(1, 1, 1 / 8f);
            client.getItemRenderer().renderItem(
                    stack,
                    ModelTransformationMode.FIXED,
                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    client.getItemRenderer().getModels().getModel(stack)
            );
            matrices.scale(1, 1, 8);
        };
    }

    static ButtonRenderer model(Identifier id) {
        return (client, entity, hitResult, vertexConsumers, matrices, light, overlay) -> {
            matrices.scale(1, 1, 1 / 8f);
            var model = BakedModelManagerHelper.getModel(MinecraftClient.getInstance().getBakedModelManager(), id);
            client.getItemRenderer().renderItem(
                    Items.STRUCTURE_VOID.getDefaultStack(),
                    ModelTransformationMode.FIXED,
                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    model != null ? model : client.getBakedModelManager().getMissingModel()
            );
            matrices.scale(1, 1, 8);
        };
    }

    @Environment(EnvType.CLIENT)
    void render(
            MinecraftClient client,
            DrawerFrameBlockEntity entity,
            BlockHitResult hitResult,
            VertexConsumerProvider vertexConsumers,
            MatrixStack matrices,
            int light,
            int overlay
    );
}