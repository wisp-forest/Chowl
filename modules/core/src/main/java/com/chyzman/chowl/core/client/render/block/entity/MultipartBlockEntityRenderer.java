package com.chyzman.chowl.core.client.render.block.entity;

import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.pond.MinecraftClientDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

public class MultipartBlockEntityRenderer implements BlockEntityRenderer<MultipartBlockEntity, VeryUnstableMultipartRenderState> {
    public MultipartBlockEntityRenderer(BlockEntityRendererFactory.Context ignored) {}

    @Override
    public VeryUnstableMultipartRenderState createRenderState() {
        return new VeryUnstableMultipartRenderState();
    }

    @Override
    public void updateRenderState(MultipartBlockEntity blockEntity, VeryUnstableMultipartRenderState state, float tickProgress, Vec3d cameraPos, ModelCommandRenderer.@Nullable CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);
        // I am probably not supposed to do this and this will probably blow up.
        state.blockEntity = blockEntity;
        state.tickDelta = tickProgress;
    }

    @Override
    public void render(VeryUnstableMultipartRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        matrices.push();
        // Please don't tell IMS
        // This is just a temporary fix while I work on a proper update for the renderer
        for (Part part : state.blockEntity.getParts()) {
            matrices.push();
            // FIXME: This is more medium-rare but still cooked
            // ((MinecraftClientDuck) MinecraftClient.getInstance()).chowl$getPartRenderDispatcher().render(part, state.tickDelta, matrices, queue);
            matrices.pop();
        }
        matrices.pop();
    }
}
