package com.chyzman.chowl.test.client.render;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.multipart.api.client.PartRendererFactory;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderer;
import com.chyzman.chowl.test.multipart.FramePanel;
import com.chyzman.chowl.test.registry.TestItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;

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
    public void extractRenderState(
        @NonNull FramePanel part,
        @NonNull FramePanelRenderState state,
        float tickProgress,
        @NotNull Vec3 cameraPos,
        ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        PartRenderer.super.extractRenderState(part, state, tickProgress, cameraPos, crumblingOverlay);
        state.face = part.getFace();
        state.item = part.getItem();
        state.upgrades = part.getUpgrades();
        state.count = part.getCount();
        state.size = part.getSize();
        //noinspection DataFlowIssue
        state.lightmapCoordinates = part.getLevel() != null ? LevelRenderer.getLightColor(part.getLevel(), part.getPos()) : 15728880;

        ItemStack stack = TestItems.FRAME_PANEL.getDefaultInstance();
        stack.set(DataComponents.ITEM_MODEL, Chowl.id("panel_base"));
        context.itemModelResolver().updateForTopItem(state.itemRenderStates.getOrCreate("remove"), stack, ItemDisplayContext.FIXED, part.getLevel(), part, 0);
        context.itemModelResolver().updateForTopItem(state.itemRenderStates.getOrCreate("item"), state.item, ItemDisplayContext.FIXED, part.getLevel(), part, 0);
    }

    @Override
    public void extractBakingRenderState(@NotNull FramePanel part, @NotNull FramePanelRenderState state) {
        PartRenderer.super.extractBakingRenderState(part, state);
        state.face = part.getFace();
        state.item = part.getItem();
        state.upgrades = part.getUpgrades();
        state.count = part.getCount();
        state.size = part.getSize();
        //noinspection DataFlowIssue
        state.lightmapCoordinates = part.getLevel() != null ? LevelRenderer.getLightColor(part.getLevel(), part.getPos()) : 15728880;

        ItemStack stack = TestItems.FRAME_PANEL.getDefaultInstance();
        stack.set(DataComponents.ITEM_MODEL, Chowl.id("panel_base"));
        context.itemModelResolver().updateForTopItem(state.itemRenderStates.getOrCreate("remove"), stack, ItemDisplayContext.FIXED, part.getLevel(), part, 0);
        context.itemModelResolver().updateForTopItem(state.itemRenderStates.getOrCreate("item"), state.item, ItemDisplayContext.FIXED, part.getLevel(), part, 0);
    }

    @Override
    public void submitForBaking(FramePanelRenderState renderState, PoseStack matrices, SubmitNodeCollector queue) {
        matrices.pushPose();
        matrices.translate(0.5, 0.5, 0.5);

        matrices.mulPose(switch (renderState.face) {
            case NORTH -> new Quaternionf();
            case WEST -> new Quaternionf().rotationY((float) (Math.PI / 2));
            case SOUTH -> new Quaternionf().rotationY((float) (Math.PI));
            case EAST -> new Quaternionf().rotationY((float) (-Math.PI / 2));
            case UP -> new Quaternionf().rotationX((float) (Math.PI / 2));
            case DOWN -> new Quaternionf().rotationX((float) (-Math.PI / 2));
        });

        matrices.translate(-0.5, -0.5, -0.5);
        matrices.pushPose();
        matrices.translate(0, 0, -7 / 16f);

        // TODO: rendering

        matrices.popPose();
        matrices.popPose();
    }

    @Override
    public void submitForRendering(FramePanelRenderState renderState, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraRenderState) {
        matrices.pushPose();
        matrices.translate(0.5, 0.5, 0.5);

//        matrices.mulPose(renderState.face.getRotation());
        matrices.mulPose(switch (renderState.face) {
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
            renderState.itemRenderStates.get("item").submit(matrices, queue, renderState.lightmapCoordinates, OverlayTexture.NO_OVERLAY, 0);

            matrices.pushPose();
            int width = context.font().width(item.getHoverName());
            matrices.translate(0, -8 / 16f, 0);
            scale = Math.min(1.5f / width, 1 / 32f);

            matrices.scale(-scale, -scale, scale);
            queue.submitText(matrices, -width / 2f, 0, item.getHoverName().getVisualOrderText(), false, Font.DisplayMode.POLYGON_OFFSET, renderState.lightmapCoordinates, 0xFFFFFFFF, 0, 0);
            matrices.popPose();

            matrices.pushPose();
            var count = Component.literal(String.valueOf(renderState.count)).append("/").append(String.valueOf(renderState.size));
            width = context.font().width(count);
            matrices.translate(0, 23 / 32f, 0);
            scale = Math.min(1.5f / width, 1 / 32f);

            matrices.scale(-scale, -scale, scale);
            queue.submitText(matrices, -width / 2f, 0, count.getVisualOrderText(), false, Font.DisplayMode.POLYGON_OFFSET, renderState.lightmapCoordinates, 0xFFFFFFFF, 0, 0);
            matrices.popPose();
        }
        matrices.popPose();
    }

    @Override
    public boolean shouldBake(FramePanel part, float tickProgress, Vec3 cameraPos) {
        return true;
    }
}
