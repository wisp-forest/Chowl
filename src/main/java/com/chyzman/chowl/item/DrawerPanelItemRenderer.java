package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.block.DrawerFrameBlockEntityRenderer;
import com.chyzman.chowl.registry.ChowlRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import java.math.BigInteger;

import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

@Environment(EnvType.CLIENT)
public class DrawerPanelItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var client = MinecraftClient.getInstance();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));
        var model = BakedModelManagerHelper.getModel(MinecraftClient.getInstance().getBakedModelManager(), id("item/drawer_panel_base"));
        if (model != null) {
            client.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, overlay, model);
        }
        if (stack.hasNbt()) {
            var nbt = stack.getNbt();
            if (nbt != null && nbt.contains("DrawerComponent")) {
                matrices.push();
                DrawerComponent drawerComponent = new DrawerComponent();
                drawerComponent.readNbt(nbt.getCompound("DrawerComponent"));
                matrices.scale(1 / 2f, 1 / 2f, 1 / 2f);
                matrices.translate(0, 0, -1 / 16f);
                client.getItemRenderer().renderItem(drawerComponent.itemVariant.toStack(), ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, overlay, client.getItemRenderer().getModels().getModel(drawerComponent.itemVariant.toStack()));
                matrices.pop();
                if (drawerComponent.count.compareTo(BigInteger.ZERO) > 0) {
                    matrices.push();
                    matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                    matrices.translate(0, -0.5 + 1 / 16f, -1 / 31f);
                    matrices.scale(1 / 40f, 1 / 40f, 1 / 40f);
                    var amount = drawerComponent.count.toString();
                    var amountWidth = client.textRenderer.getWidth(amount);
                    if (amountWidth > 36) {
                        matrices.scale(36f / amountWidth, 36f / amountWidth, 36f / amountWidth);
                    }
                    client.textRenderer.draw(amount, -amountWidth / 2f + 0.5f, 0, Colors.WHITE, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
                    matrices.pop();
                }
                if (drawerComponent.itemVariant != ItemVariant.blank()) {
                    matrices.push();
                    matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                    matrices.translate(0, 0.25, -1 / 31f);
                    matrices.scale(1 / 40f, 1 / 40f, 1 / 40f);
                    var title = drawerComponent.itemVariant.toStack().getName();
                    var titleWidth = client.textRenderer.getWidth(title);
                    if (titleWidth > 36) {
                        matrices.scale(36f / titleWidth, 36f / titleWidth, 36f / titleWidth);
                    }
                    client.textRenderer.draw(title, -titleWidth / 2f + 0.5f, 0, Colors.WHITE, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
                    matrices.pop();
                }
            }
        }
    }
}