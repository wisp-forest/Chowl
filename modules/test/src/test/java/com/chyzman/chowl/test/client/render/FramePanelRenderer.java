package com.chyzman.chowl.test.client.render;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.multipart.api.client.PartRenderer;
import com.chyzman.chowl.core.multipart.api.client.PartRendererFactory;
import com.chyzman.chowl.core.multipart.api.client.PartWithSubpartRenderer;
import com.chyzman.chowl.core.util.VoxelShapeHelper;
import com.chyzman.chowl.test.ChowlTest;
import com.chyzman.chowl.test.mixin.ItemRenderStateAccessor;
import com.chyzman.chowl.test.multipart.FramePanel;
import com.chyzman.chowl.test.registry.TestItems;
import net.minecraft.block.Block;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Quaternionf;

public class FramePanelRenderer extends PartWithSubpartRenderer<FramePanel> {
    private final ItemRenderState itemRenderState = new ItemRenderState();

    public FramePanelRenderer(PartRendererFactory.Context context) {
        super(context);
        this.addRenderer(FramePanel.RemovePart.class, new SubPartRenderer<FramePanel.RemovePart>() {
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
        });
    }

    @Override
    public void renderBaked(FramePanel part, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);

        matrixStack.multiply(switch (part.getFace()) {
            case NORTH -> new Quaternionf();
            case WEST -> new Quaternionf().rotationY((float) (Math.PI / 2));
            case SOUTH -> new Quaternionf().rotationY((float) (Math.PI));
            case EAST -> new Quaternionf().rotationY((float) (-Math.PI / 2));
            case UP -> new Quaternionf().rotationX((float) (Math.PI / 2));
            case DOWN -> new Quaternionf().rotationX((float) (-Math.PI / 2));
        });

        matrixStack.translate(-0.5, -0.5, -0.5);
        matrixStack.push();
        matrixStack.translate(0, 0, -7 / 16f);
        ItemStack stack = TestItems.FRAME_PANEL.getDefaultStack();
        stack.set(DataComponentTypes.ITEM_MODEL, Chowl.id("panel_base"));

        BlockModelRenderer blockModelRenderer = context.getRenderManager().getModelRenderer();
        context.getItemModelManager().update(itemRenderState, stack, ModelTransformationMode.NONE, false, part.getWorld(), null, 0);
        BakedModel model = ((ItemRenderStateAccessor.LayerRenderStateAccessor) ((ItemRenderStateAccessor) itemRenderState).getLayers()[0]).getModel();
        blockModelRenderer.render(part.getWorld(), model, part.getHolder().getCachedState(), part.getPos(), matrixStack, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, part.getWorld().getRandom(), 0, overlay);
        matrixStack.pop();
        renderSubPartsBaked(part, matrixStack, vertexConsumers, light, overlay);
        matrixStack.pop();
    }

    @Override
    public void renderUnbaked(FramePanel part, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);

        matrixStack.multiply(switch (part.getFace()) {
            case NORTH -> new Quaternionf();
            case WEST -> new Quaternionf().rotationY((float) (Math.PI / 2));
            case SOUTH -> new Quaternionf().rotationY((float) (Math.PI));
            case EAST -> new Quaternionf().rotationY((float) (-Math.PI / 2));
            case UP -> new Quaternionf().rotationX((float) (Math.PI / 2));
            case DOWN -> new Quaternionf().rotationX((float) (-Math.PI / 2));
        });

        matrixStack.translate(-0.5, -0.5, -0.5);
        float scale = 0.5f;
        matrixStack.translate(0.5, 0.5, 0);
        matrixStack.scale(scale, scale, scale);
        matrixStack.translate(0, 0, -1 / 32f);

        ItemStack item = part.getItem();
        if (!item.isEmpty()) {
            context.getItemRenderer().renderItem(item, ModelTransformationMode.FIXED, light, overlay, matrixStack, vertexConsumers, part.getWorld(), 0);

            matrixStack.push();
            int width = context.getTextRenderer().getWidth(item.getName());
            matrixStack.translate(0, -8 / 16f, 0);
            scale = Math.min(1.5f / width, 1 / 32f);

            matrixStack.scale(-scale, -scale, scale);
            context.getTextRenderer().draw(item.getName(), -width / 2f, 0, 0xFFFFFF, false, matrixStack.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.POLYGON_OFFSET, 0, light, false);
            matrixStack.pop();

            matrixStack.push();
            MutableText count = Text.literal(String.valueOf(part.getCount())).append("/").append(String.valueOf(part.getSize()));
            width = context.getTextRenderer().getWidth(count);
            matrixStack.translate(0, 23 / 32f, 0);
            scale = Math.min(1.5f / width, 1 / 32f);

            matrixStack.scale(-scale, -scale, scale);
            context.getTextRenderer().draw(count, -width / 2f, 0, 0xFFFFFF, false, matrixStack.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.POLYGON_OFFSET, 0, light, false);
            matrixStack.pop();
        }
        matrixStack.pop();
    }

    @Override
    public boolean shouldBakePart(FramePanel entity) {
        return true;
    }
}
