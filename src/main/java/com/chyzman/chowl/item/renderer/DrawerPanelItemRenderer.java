package com.chyzman.chowl.item.renderer;

import com.chyzman.chowl.classes.FunniVertexConsumer;
import com.chyzman.chowl.classes.FunniVertexConsumerProvider;
import com.chyzman.chowl.item.DrawerPanelItem;
import com.chyzman.chowl.item.component.DisplayingPanelItem;
import com.chyzman.chowl.item.component.UpgradeablePanelItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MeshBuilderImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.ItemRenderContext;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.math.BigInteger;
import java.util.Arrays;

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

        var baseModel = MinecraftClient.getInstance().getBakedModelManager().getModel(id("item/drawer_panel_base"));
        if (baseModel != null) {
            client.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, overlay, baseModel);
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
            matrices.push();
            var provider = new FunniVertexConsumerProvider();
            client.getItemRenderer().renderItem(displayStack, ModelTransformationMode.FIXED, false, new MatrixStack(), provider, glowing ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light, overlay, client.getItemRenderer().getModels().getModel(displayStack));
            final Vec3d[] bounds = {new Vec3d(0, 0, 0), new Vec3d(0, 0, 0)};
            FunniVertexConsumer consumer = (FunniVertexConsumer) provider.getBuffer(null);
            consumer.vertices.forEach(vertex -> {
                bounds[0] = new Vec3d(Math.min(bounds[0].x, vertex.x), Math.min(bounds[0].y, vertex.y), Math.min(bounds[0].z, vertex.z));
                bounds[1] = new Vec3d(Math.max(bounds[1].x, vertex.x), Math.max(bounds[1].y, vertex.y), Math.max(bounds[1].z, vertex.z));
            });
            consumer.vertices.clear();
            var size = bounds[1].subtract(bounds[0]);
            if (mode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND) {
                float precision = 100;
                MinecraftClient.getInstance().player.sendMessage(Text.of("(" + ((int) (size.x * precision)) / precision + "," + ((int) (size.y * precision)) / precision + "," + ((int) (size.z * precision)) / precision + ")"), true);
            }
            matrices.pop();

            float top = 0f;
            float bottom = 0f;
            if (!customization.hideName()) {
                matrices.push();
                matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                matrices.translate(0, 3 / 8f, -1 / 31f);
                matrices.scale(1 / 40f, 1 / 40f, 1 / 40f);
                MutableText title = (MutableText) displayStack.getName();
                var titleWidth = client.textRenderer.getWidth(title);
                if (titleWidth > maxwidth) {
                    matrices.scale(maxwidth / titleWidth, maxwidth / titleWidth, maxwidth / titleWidth);
                    top = (maxwidth / titleWidth) / client.textRenderer.fontHeight;
                } else {
                    top = 1f / client.textRenderer.fontHeight;
                }


                matrices.translate(0, -client.textRenderer.fontHeight + 1f, 0);
                client.textRenderer.draw(drawerPanel.styleText(stack, title), -titleWidth / 2f + 0.5f, 0, Colors.WHITE, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, glowing ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light);
                matrices.pop();
            }
            if (count.compareTo(BigInteger.ZERO) > 0 && !customization.hideCount()) {
                matrices.push();
                matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                matrices.translate(0, -3 / 8f, -1 / 31f);
                matrices.scale(1 / 40f, 1 / 40f, 1 / 40f);
                var cap = stack.get(CAPACITY).compareTo(BigInteger.valueOf(CHOWL_CONFIG.max_capacity_level_before_exponents())) > 0 ? "2^" + stack.get(CAPACITY).add(BigInteger.valueOf(11)) : drawerPanel.capacity(stack);
                var amount = count + "/" + cap;
                var amountWidth = client.textRenderer.getWidth(amount);
                if (amountWidth > maxwidth) {
                    matrices.scale(maxwidth / amountWidth, maxwidth / amountWidth, maxwidth / amountWidth);
                    bottom = (maxwidth / amountWidth) / client.textRenderer.fontHeight;
                } else {
                    bottom = 1f / client.textRenderer.fontHeight;
                }

                client.textRenderer.draw(drawerPanel.styleText(stack, Text.literal(amount)), -amountWidth / 2f + 0.5f, 0, Colors.WHITE, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, glowing ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light);
                matrices.pop();
            }
            if (!customization.hideItem()) {
                float scale = (float) Math.min(2, (1 / (Math.max(size.x, Math.max(size.y, size.z)))));
                matrices.push();
                matrices.translate(0, 0, -1 / 32f);
                matrices.scale(scale, scale, scale);
                scale = (12 / 16f);
                matrices.scale(scale, scale, scale);
                matrices.translate(0, ((top - bottom)/2), 0);
                scale = 1 - ((top + bottom) * 3);
                matrices.scale(scale, scale, scale);
//                scale = 0.9f;
//                matrices.scale(scale, scale, scale);
                matrices.push();
                client.getItemRenderer().renderItem(displayStack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, glowing ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light, overlay, client.getItemRenderer().getModels().getModel(displayStack));
                matrices.pop();
                matrices.pop();
            }
        }
    }
}