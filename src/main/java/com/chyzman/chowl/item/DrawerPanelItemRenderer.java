package com.chyzman.chowl.item;

import com.chyzman.chowl.classes.FunniVertexConsumerProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.Colors;
import net.minecraft.util.math.RotationAxis;

import java.math.BigInteger;

import static com.chyzman.chowl.classes.FunniVertexConsumerProvider.consumer;
import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

@Environment(EnvType.CLIENT)
@SuppressWarnings("UnstableApiUsage")
public class DrawerPanelItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var client = MinecraftClient.getInstance();
        float maxwidth = 30;
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));
        var model = BakedModelManagerHelper.getModel(MinecraftClient.getInstance().getBakedModelManager(), id("item/drawer_panel_base"));
        if (model != null) {
            client.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, overlay, model);
        }
        if (stack.hasNbt()) {
            var nbt = stack.getNbt();
            if (nbt != null && nbt.contains("DrawerComponent")) {
                DrawerComponent drawerComponent = new DrawerComponent();
                drawerComponent.readNbt(nbt.getCompound("DrawerComponent"));
                if (!drawerComponent.itemVariant.isBlank() && !drawerComponent.config.hideItem) {
                    matrices.push();
                    matrices.scale(1 / 3f, 1 / 3f, 1 / 3f);
                    matrices.translate(0, 0, -3 / 32f);
                    client.getItemRenderer().renderItem(drawerComponent.itemVariant.toStack(), ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, overlay, client.getItemRenderer().getModels().getModel(drawerComponent.itemVariant.toStack()));
                    matrices.pop();
                }
                if (drawerComponent.count.compareTo(BigInteger.ZERO) > 0 && !drawerComponent.config.hideCount) {
                    matrices.push();
                    matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                    matrices.translate(0, -3 / 8f, -1 / 31f);
                    matrices.scale(1 / 40f, 1 / 40f, 1 / 40f);
                    var amount = drawerComponent.count.toString();
                    var amountWidth = client.textRenderer.getWidth(amount);
                    if (amountWidth > maxwidth) {
                        matrices.scale(maxwidth / amountWidth, maxwidth / amountWidth, maxwidth / amountWidth);
                    }
                    client.textRenderer.draw(drawerComponent.styleText(amount), -amountWidth / 2f + 0.5f, 0, Colors.WHITE, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
                    matrices.pop();
                }
                if (drawerComponent.itemVariant != ItemVariant.blank() && !drawerComponent.config.hideName) {
                    matrices.push();
                    matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                    matrices.translate(0, 3 / 8f, -1 / 31f);
                    matrices.scale(1 / 40f, 1 / 40f, 1 / 40f);
                    MutableText title = (MutableText) drawerComponent.itemVariant.toStack().getName();
                    var titleWidth = client.textRenderer.getWidth(title);
                    if (titleWidth > maxwidth) {
                        matrices.scale(maxwidth / titleWidth, maxwidth / titleWidth, maxwidth / titleWidth);
                    }
                    matrices.translate(0, -client.textRenderer.fontHeight + 1f, 0);
                    client.textRenderer.draw(drawerComponent.styleText(title), -titleWidth / 2f + 0.5f, 0, Colors.WHITE, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
                    matrices.pop();
                }
            }
        }
    }
}