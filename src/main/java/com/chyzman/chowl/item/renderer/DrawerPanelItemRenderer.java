package com.chyzman.chowl.item.renderer;

import com.chyzman.chowl.item.DrawerPanelItem;
import com.chyzman.chowl.item.component.DisplayingPanelItem;
import com.chyzman.chowl.item.component.UpgradeablePanelItem;
import com.chyzman.chowl.util.BigIntUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.RotationAxis;

import java.math.BigInteger;

import static com.chyzman.chowl.Chowl.*;
import static com.chyzman.chowl.item.DrawerPanelItem.CAPACITY;
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

        var model = MinecraftClient.getInstance().getBakedModelManager().getModel(id("item/drawer_panel_base"));
        if (model != null) {
            client.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, overlay, model);
        }

        if (!(stack.getItem() instanceof DrawerPanelItem drawerPanel)) return;

        var displayStack = drawerPanel.displayedVariant(stack).toStack();
        var count = drawerPanel.displayedCount(stack);
        var customization = stack.get(DisplayingPanelItem.CONFIG);
        var glowing = false;
        if (stack.getItem() instanceof UpgradeablePanelItem panel) {
            if (panel.hasUpgrade(stack, upgrade -> upgrade.isIn(GLOWING_UPGRADE_TAG))) {
                glowing = true;
            }
        }

        if (!displayStack.isEmpty()) {
            if (!customization.hideItem()) {
                matrices.push();
                matrices.scale(1 / 3f, 1 / 3f, 1 / 3f);
                matrices.translate(0, 0, -3 / 32f);
                client.getItemRenderer().renderItem(displayStack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, glowing ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light, overlay, client.getItemRenderer().getModels().getModel(displayStack));
                matrices.pop();
            }
            if (!customization.hideName()) {
                matrices.push();
                matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                matrices.translate(0, 3 / 8f, -1 / 31f);
                matrices.scale(1 / 40f, 1 / 40f, 1 / 40f);
                MutableText title = (MutableText) displayStack.getName();
                var titleWidth = client.textRenderer.getWidth(title);
                if (titleWidth > maxwidth) {
                    matrices.scale(maxwidth / titleWidth, maxwidth / titleWidth, maxwidth / titleWidth);
                }
                matrices.translate(0, -client.textRenderer.fontHeight + 1f, 0);
                client.textRenderer.draw(drawerPanel.styleText(stack, title), -titleWidth / 2f + 0.5f, 0, Colors.WHITE, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, glowing ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light);
                matrices.pop();
            }
        }

        if (count.compareTo(BigInteger.ZERO) > 0 && !customization.hideCount()) {
            matrices.push();
            matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
            matrices.translate(0, -3 / 8f, -1 / 31f);
            matrices.scale(1 / 40f, 1 / 40f, 1 / 40f);
            var cap = stack.get(CAPACITY).compareTo(BigInteger.valueOf(CHOWL_CONFIG.max_capacity_level_before_exponents())) > 0 ? "2^" + stack.get(CAPACITY).add(BigInteger.valueOf(11)) : DrawerPanelItem.getCapacity(stack);
            var amount = count + "/" + cap;
            var amountWidth = client.textRenderer.getWidth(amount);
            if (amountWidth > maxwidth) {
                matrices.scale(maxwidth / amountWidth, maxwidth / amountWidth, maxwidth / amountWidth);
            }
            client.textRenderer.draw(drawerPanel.styleText(stack, Text.literal(amount)), -amountWidth / 2f + 0.5f, 0, Colors.WHITE, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, glowing ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light);
            matrices.pop();
        }
    }
}