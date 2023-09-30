package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.client.RenderGlobals;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.Colors;
import net.minecraft.util.math.RotationAxis;

import java.math.BigInteger;

import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

@Environment(EnvType.CLIENT)
@SuppressWarnings("UnstableApiUsage")
public class MirrorPanelItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var client = MinecraftClient.getInstance();
        float maxwidth = 30;
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));
        var model = client.getBakedModelManager().getModel(id("item/mirror_panel_base"));
        if (model != null) {
            client.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, overlay, model);
        }

        if (stack.has(MirrorPanelItem.FILTER)) {
            var filterVariant = stack.get(MirrorPanelItem.FILTER);

            if (!filterVariant.isBlank()) {
                matrices.push();
                matrices.scale(1 / 2f, 1 / 2f, 1 / 2f);
                matrices.translate(0, 0, -1 / 16f);
                client.getItemRenderer().renderItem(filterVariant.toStack(), ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, overlay, client.getItemRenderer().getModels().getModel(filterVariant.toStack()));
                matrices.pop();

                matrices.push();
                matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                matrices.translate(0, 3/16f, -1 / 31f);
                matrices.scale(1 / 40f, 1 / 40f, 1 / 40f);
                MutableText title = (MutableText) filterVariant.toStack().getName();
                var titleWidth = client.textRenderer.getWidth(title);
                if (titleWidth > maxwidth) {
                    matrices.scale(maxwidth / titleWidth, maxwidth / titleWidth, maxwidth / titleWidth);
                }
                client.textRenderer.draw(title, -titleWidth / 2f + 0.5f, 0, Colors.WHITE, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
                matrices.pop();
            }

            DrawerFrameBlockEntity drawerFrame = RenderGlobals.DRAWER_FRAME.get();
            if (drawerFrame == null) return;

            var storage = ((MirrorPanelItem) stack.getItem()).getStorage(stack, drawerFrame, RenderGlobals.FRAME_SIDE.get());

            if (!(storage instanceof SingleSlotStorage<ItemVariant> singleSlot)) return;

            if (singleSlot.getAmount() > 0) {
                matrices.push();
                matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                matrices.translate(0, -3/8f, -1 / 31f);
                matrices.scale(1 / 40f, 1 / 40f, 1 / 40f);
                var amount = Long.toString(singleSlot.getAmount());
                var amountWidth = client.textRenderer.getWidth(amount);
                if (amountWidth > maxwidth) {
                    matrices.scale(maxwidth / amountWidth, maxwidth / amountWidth, maxwidth / amountWidth);
                }
                client.textRenderer.draw(amount, -amountWidth / 2f + 0.5f, 0, Colors.WHITE, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
                matrices.pop();
            }
        }
    }
}