package com.chyzman.chowl.industries.item.renderer;

import com.chyzman.chowl.core.client.util.RenderCounter;
import com.chyzman.chowl.industries.client.RenderGlobals;
import com.chyzman.chowl.industries.item.PackingPanelItem;
import com.chyzman.chowl.industries.item.component.BareItemsComponent;
import com.chyzman.chowl.industries.item.component.DisplayingPanelItem;
import com.chyzman.chowl.industries.item.component.UpgradeablePanelItem;
import com.chyzman.chowl.industries.registry.ChowlComponents;
import com.chyzman.chowl.industries.transfer.BigStorageView;
import com.chyzman.chowl.industries.transfer.FakeStorageView;
import com.chyzman.chowl.industries.transfer.PanelStorageContext;
import com.chyzman.chowl.industries.util.EasterEggUtil;
import com.chyzman.chowl.industries.util.ItemScalingUtil;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.chyzman.chowl.industries.Chowl.GLOWING_UPGRADE_TAG;
import static com.chyzman.chowl.industries.util.FormatUtil.formatCount;

@Environment(EnvType.CLIENT)
public class GenericPanelItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final float MAX_WIDTH = 30;

    private final Identifier baseModelId;

    public GenericPanelItemRenderer(Identifier baseModelId) {
        this.baseModelId = baseModelId;
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!RenderCounter.shouldRender()) return;

        try (var ignored = RenderCounter.enterRender()) {
            var client = MinecraftClient.getInstance();

            matrices.translate(0.5, 0.5, 0.5);
            if (!RenderGlobals.IN_FRAME) {
                if (stack.contains(DataComponentTypes.CUSTOM_NAME)) {
                    var easterEgg = EasterEggUtil.EasterEgg.findEasterEgg(stack.getName().getString());
                    if (easterEgg != null && easterEgg.orientationModifier != null) {
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90 * easterEgg.orientationModifier.apply(0)));
                    }
                }
            }

            var baseModel = client.getBakedModelManager().getModel(baseModelId);
            if (baseModel != null && RenderGlobals.BAKED.get() != Boolean.TRUE) {
                matrices.push();
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                client.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, overlay, baseModel);
                matrices.pop();
            }
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
            matrices.translate(-0.5, -0.5, -1 / 16f - 1 / 512f);
            drawUI(stack, mode, matrices, vertexConsumers, light, overlay);
            matrices.pop();
        }
    }

    protected void drawUI(ItemStack panelStack, ModelTransformationMode panelTransformationMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var client = MinecraftClient.getInstance();

        if (!(panelStack.getItem() instanceof DisplayingPanelItem panel)) return;

        var storage = panel.getStorage(PanelStorageContext.forRendering(panelStack));

        if (storage == null) return;

        matrices.push();

        List<StorageView<ItemVariant>> slots = new ArrayList<>(storage.getSlots());
        slots.removeIf(x -> (x instanceof FakeStorageView fake && !fake.countInDisplay()));

        var size = 256;
        var adapter = OwoUIAdapter.createWithoutScreen(0, 0, size, size, (sizing, sizing2) -> Containers.verticalFlow(Sizing.fixed(size), Sizing.fixed(size)));

        adapter.rootComponent
                .child(Components.label(Text.literal("test")))
                .child(Components.item(slots.getFirst().getResource().toStack()))
                .child(Components.label(Text.literal("test")))
                .allowOverflow(true);

        var drawContext = new DrawContext(client, client.getBufferBuilders().getEntityVertexConsumers());
        drawContext.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        drawContext.scale((float) 1 / size, (float) 1 / size, -(float) 1 / size / size);
        adapter.inflateAndMount();
        adapter.toggleInspector();
        adapter.toggleGlobalInspector();
        adapter.render(drawContext, 0, 0, client.getRenderTickCounter().getTickDelta(true));
        drawContext.draw();
        adapter.dispose();
        matrices.pop();
    }


    protected void drawDisplays(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var client = MinecraftClient.getInstance();

        if (!(stack.getItem() instanceof DisplayingPanelItem panel)) return;

        var storage = panel.getStorage(PanelStorageContext.forRendering(stack));

        if (storage == null) return;

        matrices.translate(0, 0, -1 / 16f - 0.001);
        matrices.push();

        List<StorageView<ItemVariant>> slots = new ArrayList<>(storage.getSlots());
        slots.removeIf(x -> (x instanceof FakeStorageView fake && !fake.countInDisplay()));

        matrices.scale(3 / 4f, 3 / 4f, 3 / 4f);

        var renderScale = 1 / Math.ceil(Math.sqrt(slots.size()));

        matrices.translate(0.5, 0.5, 0);
        matrices.translate(-renderScale * 1.5f, -renderScale * 1.5f, 0);
        matrices.scale((float) (renderScale), (float) (renderScale), (float) (renderScale));

        var customization = DisplayingPanelItem.getConfig(stack);
        var glowing = false;

        if (stack.getItem() instanceof UpgradeablePanelItem upgradeable) {
            if (upgradeable.hasUpgrade(stack, upgrade -> upgrade.isIn(GLOWING_UPGRADE_TAG))) {
                glowing = true;
            }
        }

        var totalCount = stack.getOrDefault(ChowlComponents.BARE_ITEMS, BareItemsComponent.DEFAULT).totalCount();

        for (int i = 0; i < slots.size(); i++) {
            StorageView<ItemVariant> slot = slots.get(i);

            matrices.push();
            matrices.translate(1 - i % (1 / renderScale), 1 - (float) (int) (i / (1 / renderScale)), 0);

            if (slots.size() <= 1) {
                matrices.scale(4 / 3f, 4 / 3f, 4 / 3f);
            } else {
                var number = 5 / 4f;
                matrices.scale(number, number, number);
            }

//            var count = panel.displayedCount(stack, RenderGlobals.DRAWER_FRAME.get(), RenderGlobals.FRAME_SIDE.get());

            BigInteger count = BigStorageView.bigAmount(slot);

            if (!slot.isResourceBlank()) {
                ItemStack displayStack = slot.getResource().toStack();
                var properties = ItemScalingUtil.getItemModelProperties(displayStack);

                if (!customization.hideName()) {
                    matrices.push();
                    matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                    matrices.translate(0, 3 / 8f, 0);
                    matrices.scale(1 / 40f, 1 / 40f, 1 / 40f);

                    AtomicReference<MutableText> title = new AtomicReference<>((MutableText) displayStack.getName());
                    if (stack.contains(DataComponentTypes.CUSTOM_NAME) && EasterEggUtil.EasterEgg.findEasterEgg(stack.getName().getString()) == null) {
                        title.set((MutableText) stack.getName());
                    }
                    var titleWidth = client.textRenderer.getWidth(title.get());
                    if (titleWidth > MAX_WIDTH) {
                        matrices.scale(MAX_WIDTH / titleWidth, MAX_WIDTH / titleWidth, MAX_WIDTH / titleWidth);
                    }

                    matrices.translate(0, -client.textRenderer.fontHeight + 1f, 0);
                    client.textRenderer.draw(panel.styleText(stack, title.get()), -titleWidth / 2f + 0.5f, 0, Colors.WHITE, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, glowing ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light);
                    matrices.pop();
                }

                if (count.compareTo(BigInteger.ZERO) > 0 && (!customization.hideCount() || !customization.hideCapacity())) {
                    matrices.push();
                    matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                    matrices.translate(0, -3 / 8f, 0);
                    matrices.scale(1 / 40f, 1 / 40f, 1 / 40f);

                    StringBuilder countText = new StringBuilder();

                    if (!customization.hideCount()) {
                        countText.append(formatCount(count));
                    }

                    var capacity = BigStorageView.bigCapacity(slot);

                    if (stack.getItem() instanceof PackingPanelItem) capacity = capacity.subtract(totalCount).add(count);

                    if (!customization.hideCapacity()) {
                        if (!customization.hideCount()) countText.append("/");

                        countText.append(formatCount(capacity));
                    }

                    var amountWidth = client.textRenderer.getWidth(countText.toString());
                    if (amountWidth > MAX_WIDTH) {
                        matrices.scale(MAX_WIDTH / amountWidth, MAX_WIDTH / amountWidth, MAX_WIDTH / amountWidth);
                    }

                    //TODO make this not hardcoded/jank
                    var color = Colors.WHITE;
                    var space = capacity.subtract(count);
                    if (space.compareTo(BigInteger.valueOf(displayStack.getMaxCount())) <= 0) color = DyeColor.ORANGE.getSignColor();
                    if (space.compareTo(BigInteger.ZERO) <= 0) color = DyeColor.RED.getSignColor();

                    client.textRenderer.draw(panel.styleText(stack, Text.literal(countText.toString())).copy().withColor(color), -amountWidth / 2f + 0.5f, 0, Colors.WHITE, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, glowing ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light);
                    matrices.pop();
                }

                if (!customization.hideItem()) {
                    var framed = RenderGlobals.IN_FRAME;
                    RenderGlobals.IN_FRAME = false;
                    ItemScalingUtil.renderScaledItem(
                            displayStack,
                            matrices,
                            vertexConsumers,
                            glowing ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light,
                            overlay,
                            matrixStack -> {
                                if (!customization.hideName()) matrices.translate(0, 0, 0);
                                float scale;
                                if (mode == ModelTransformationMode.GUI) {
                                    scale = 0.47f;
                                } else {
                                    scale = 0.4f;
                                }
                                matrices.scale(scale, scale, scale);
                            }
                    );
                    RenderGlobals.IN_FRAME = framed;
                }
            }
            matrices.pop();
        }
        matrices.pop();

        if (customization.showPercentage() && RenderGlobals.IN_FRAME) {
            var fullPercent = new BigDecimal(BigStorageView.bigAmount(slots.get(0)))
                    .divide(new BigDecimal(BigStorageView.bigCapacity(slots.get(0)).max(BigInteger.ONE)), MathContext.DECIMAL32)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
            Double roundedPercent = (double) Math.round(fullPercent * 100) / 100;
            var percent = roundedPercent + "%";

            drawPercent(stack, panel, matrices, vertexConsumers, client, percent, glowing, light, overlay);
        }
    }

    protected void drawPercent(ItemStack stack, DisplayingPanelItem panel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, MinecraftClient client, String percent, boolean glowing, int light, int overlay) {
        matrices.push();
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
        matrices.translate(0, -4 / 8f, 0);
        matrices.scale(1 / 48f, 1 / 48f, 1 / 48f);
        matrices.scale(1 / 2f, 1 / 2f, 1 / 2f);

        var percentWidth = client.textRenderer.getWidth(percent);

        matrices.translate(0, client.textRenderer.fontHeight * 0.25f, 0);
        client.textRenderer.draw(panel.styleText(stack, Text.literal(percent)), -percentWidth / 2f + 0.5f, 0, Colors.WHITE, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, glowing ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light);
        matrices.pop();
    }
}
