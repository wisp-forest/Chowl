package com.chyzman.chowl.test.client.render;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.multipart.api.client.PartRendererFactory;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderer;
import com.chyzman.chowl.test.multipart.FramePanel;
import com.chyzman.chowl.test.registry.TestItems;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class FramePanelRenderer implements PartRenderer<FramePanel, FramePanelRenderState> {
    private final PartRendererFactory.Context context;

    public FramePanelRenderer(PartRendererFactory.Context context) {
        this.context = context;
        /*this.addRenderer(FramePanel.RemovePart.class, new SubPartRenderer<FramePanel.RemovePart>() {
            @Override
            public void renderBaked(FramePanel.RemovePart part, FramePanel parent, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
                matrices.push();
                float scale = 2 / 16f;
                matrices.scale(scale, scale, scale);
                matrices.translate(0, 7, -8 / 16f - 1 / 32f);
                ItemStack stack = TestItems.FRAME_PANEL.getDefaultStack();
                stack.set(DataComponentTypes.ITEM_MODEL, Chowl.id("remove"));

                BlockModelRenderer blockModelRenderer = context.getRenderManager().getModelRenderer();
                context.getItemModelManager().update(itemRenderState, stack, ModelTransformationMode.NONE, false, parent.getWorld(), null, 0);
                BakedModel model = ((ItemRenderStateAccessor.LayerRenderStateAccessor) ((ItemRenderStateAccessor) itemRenderState).getLayers()[0]).getModel();
                blockModelRenderer.render(parent.getWorld(), model, parent.getHolder().getCachedState(), parent.getPos(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, parent.getWorld().getRandom(), 0, overlay);
                matrices.pop();
            }

            @Override
            public void renderUnbaked(FramePanel.RemovePart part, FramePanel parent, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {}

            @Override
            public boolean shouldBake(FramePanel.RemovePart part, FramePanel parent) {
                return true;
            }
        });*/
    }

    @Override
    public @NotNull FramePanelRenderState createRenderState() {
        return new FramePanelRenderState();
    }

    @Override
    public void updateRenderState(@NotNull FramePanel part, @NotNull FramePanelRenderState state, float tickProgress, @NotNull Vec3d cameraPos, ModelCommandRenderer.@Nullable CrumblingOverlayCommand crumblingOverlay) {
        PartRenderer.super.updateRenderState(part, state, tickProgress, cameraPos, crumblingOverlay);
        state.face = part.getFace();
        state.item = part.getItem();
        state.upgrades = part.getUpgrades();
        state.count = part.getCount();
        state.size = part.getSize();

        ItemStack stack = TestItems.FRAME_PANEL.getDefaultStack();
        stack.set(DataComponentTypes.ITEM_MODEL, Chowl.id("panel_base"));
        context.itemModelResolver().clearAndUpdate(state.removeButtonRenderState, stack, ItemDisplayContext.ON_SHELF, part.getWorld(), part, 0);
    }

    @Override
    public void renderBaked(FramePanelRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);

        matrices.multiply(switch (renderState.face) {
            case NORTH -> new Quaternionf();
            case WEST -> new Quaternionf().rotationY((float) (Math.PI / 2));
            case SOUTH -> new Quaternionf().rotationY((float) (Math.PI));
            case EAST -> new Quaternionf().rotationY((float) (-Math.PI / 2));
            case UP -> new Quaternionf().rotationX((float) (Math.PI / 2));
            case DOWN -> new Quaternionf().rotationX((float) (-Math.PI / 2));
        });

        matrices.translate(-0.5, -0.5, -0.5);
        matrices.push();
        matrices.translate(0, 0, -7 / 16f);

        // TODO: rendering

        matrices.pop();
        matrices.pop();
    }

    @Override
    public void renderUnbaked(FramePanelRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);

        matrices.multiply(switch (renderState.face) {
            case NORTH -> new Quaternionf();
            case WEST -> new Quaternionf().rotationY((float) (Math.PI / 2));
            case SOUTH -> new Quaternionf().rotationY((float) (Math.PI));
            case EAST -> new Quaternionf().rotationY((float) (-Math.PI / 2));
            case UP -> new Quaternionf().rotationX((float) (Math.PI / 2));
            case DOWN -> new Quaternionf().rotationX((float) (-Math.PI / 2));
        });

        matrices.translate(-0.5, -0.5, -0.5);
        float scale = 0.5f;
        matrices.translate(0.5, 0.5, 0);
        matrices.scale(scale, scale, scale);
        matrices.translate(0, 0, -1 / 32f);

        ItemStack item = renderState.item;
        if (!item.isEmpty()) {
            // context.getItemRenderer().renderItem(item, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, part.getWorld(), 0);

            matrices.push();
            int width = context.font().getWidth(item.getName());
            matrices.translate(0, -8 / 16f, 0);
            scale = Math.min(1.5f / width, 1 / 32f);

            matrices.scale(-scale, -scale, scale);
            // context.getTextRenderer().draw(item.getName(), -width / 2f, 0, 0xFFFFFF, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.POLYGON_OFFSET, 0, light, false);
            matrices.pop();

            matrices.push();
            MutableText count = Text.literal(String.valueOf(renderState.count)).append("/").append(String.valueOf(renderState.size));
            width = context.font().getWidth(count);
            matrices.translate(0, 23 / 32f, 0);
            scale = Math.min(1.5f / width, 1 / 32f);

            matrices.scale(-scale, -scale, scale);
            // context.getTextRenderer().draw(count, -width / 2f, 0, 0xFFFFFF, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.POLYGON_OFFSET, 0, light, false);
            matrices.pop();
        }
        matrices.pop();
    }

    @Override
    public boolean shouldBake(FramePanel part, float tickProgress, Vec3d cameraPos) {
        return true;
    }
}
