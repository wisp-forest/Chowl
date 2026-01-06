package com.chyzman.chowl.core.client.render.block.entity;

import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import com.chyzman.chowl.core.client.render.block.entity.state.MultipartBlockEntityRenderState;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderDispatcher;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderManager;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderer;
import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import com.chyzman.chowl.core.multipart.pond.MinecraftClientDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultipartBlockEntityRenderer implements BlockEntityRenderer<MultipartBlockEntity, MultipartBlockEntityRenderState> {
    public MultipartBlockEntityRenderer(BlockEntityRendererFactory.Context ignored) {}

    @Override
    public MultipartBlockEntityRenderState createRenderState() {
        return new MultipartBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(MultipartBlockEntity blockEntity, MultipartBlockEntityRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);
        PartRenderDispatcher dispatcher = ((MinecraftClientDuck) MinecraftClient.getInstance()).chowl$getPartRenderDispatcher();

        PartRenderManager.scheduleBlockEntity(blockEntity);
        for (Part part : blockEntity.getParts()) {
            PartRenderer<Part, PartRenderState> partRenderer = dispatcher.get(part);
            if (partRenderer == null) {
                /*CrashReport crashReport = CrashReport.create(new IllegalStateException("Part type doesn't have an associated renderer"), "Baked Multipart Rendering");
                CrashReportSection crashReportSection = crashReport.addElement("Multipart render details");
                crashReportSection.add("Part class", part.getClass().getCanonicalName());
                crashReportSection.add("Part type", DataFlow.tryOrDefault(part.getType(), type -> type.getClass().getCanonicalName(), "null"));

                throw new CrashException(crashReport);*/
                continue;
            }

            partRenderer.enteredRenderCycle(part, tickProgress, cameraPos);
            state.partsToRender.add(dispatcher.getRenderState(part, tickProgress, crumblingOverlay));
        }
    }

    @Override
    public void render(@NotNull MultipartBlockEntityRenderState state, @NotNull MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        PartRenderDispatcher dispatcher = ((MinecraftClientDuck) MinecraftClient.getInstance()).chowl$getPartRenderDispatcher();

        matrices.push();
        for (PartRenderState partState : state.partsToRender) {
            matrices.push();
            dispatcher.render(partState, matrices, queue, cameraState);
            matrices.pop();
        }
        matrices.pop();
    }
}
