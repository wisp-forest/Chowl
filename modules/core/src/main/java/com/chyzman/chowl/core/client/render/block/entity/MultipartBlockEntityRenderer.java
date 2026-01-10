package com.chyzman.chowl.core.client.render.block.entity;

import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import com.chyzman.chowl.core.client.render.block.entity.state.MultipartBlockEntityRenderState;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderDispatcher;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderManager;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderer;
import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import com.chyzman.chowl.core.multipart.pond.MinecraftClientDuck;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultipartBlockEntityRenderer implements BlockEntityRenderer<MultipartBlockEntity, MultipartBlockEntityRenderState> {
    public MultipartBlockEntityRenderer(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public MultipartBlockEntityRenderState createRenderState() {
        return new MultipartBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(MultipartBlockEntity blockEntity, MultipartBlockEntityRenderState state, float tickProgress, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        final ProfilerFiller profiler = Profiler.get();
        profiler.push("chowl:unbakedPartRenderState");
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);
        PartRenderDispatcher dispatcher = ((MinecraftClientDuck) Minecraft.getInstance()).chowl$getPartRenderDispatcher();

        PartRenderManager.scheduleBlockEntity(blockEntity);
        for (Part part : blockEntity.getParts()) {
            PartRenderer<Part, PartRenderState> partRenderer = dispatcher.get(part);
            if (partRenderer == null) {
                continue;
            }

            partRenderer.enteredRenderCycle(part, tickProgress, cameraPos);
            state.partsToRender.add(dispatcher.getRenderState(part, tickProgress, crumblingOverlay));
        }
        profiler.pop();
    }

    @Override
    public void submit(@NotNull MultipartBlockEntityRenderState state, @NotNull PoseStack matrices, @NotNull SubmitNodeCollector queue, @NotNull CameraRenderState cameraState) {
        final ProfilerFiller profiler = Profiler.get();
        profiler.push("chowl:unbakedPartSubmit");
        PartRenderDispatcher dispatcher = ((MinecraftClientDuck) Minecraft.getInstance()).chowl$getPartRenderDispatcher();

        matrices.pushPose();
        for (PartRenderState partState : state.partsToRender) {
            matrices.pushPose();
            dispatcher.submit(partState, matrices, queue, cameraState);
            matrices.popPose();
        }
        matrices.popPose();
        profiler.pop();
    }
}
