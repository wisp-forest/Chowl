package com.chyzman.chowl.core.attachables.api.client;

import com.chyzman.chowl.core.attachables.api.Attachable;
import com.chyzman.chowl.core.attachables.api.AttachableType;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class AttachableRenderDispatcher implements ResourceManagerReloadListener {
    private Map<AttachableType<?>, AttachableRenderer<?>> renderers = ImmutableMap.of();

    public Level world;
    public Camera camera;
    public HitResult crosshairTarget;

    private final ItemRenderer itemRenderer;
    private final ItemModelResolver itemModelManager;
    private final BlockRenderDispatcher blockRenderManager;
    private final BlockEntityRenderDispatcher blockEntityRenderManager;
    private final EntityRenderDispatcher entityRenderManager;
    private final Font textRenderer;

    public AttachableRenderDispatcher(
        ItemRenderer itemRenderer,
        ItemModelResolver itemModelManager,
        BlockRenderDispatcher blockRenderManager,
        BlockEntityRenderDispatcher blockEntityRenderManager,
        EntityRenderDispatcher entityRenderManager,
        Font textRenderer
    ) {
        this.itemRenderer = itemRenderer;
        this.itemModelManager = itemModelManager;
        this.blockRenderManager = blockRenderManager;
        this.blockEntityRenderManager = blockEntityRenderManager;
        this.entityRenderManager = entityRenderManager;
        this.textRenderer = textRenderer;
    }

    @Nullable
    public <A extends Attachable> AttachableRenderer<A> get(A attachableState) {
        return (AttachableRenderer<A>) this.renderers.get(attachableState.getType());
    }

    public void configure(Level world, Camera camera, HitResult crosshairTarget) {
        if (this.world != world) {
            this.setWorld(world);
        }

        this.camera = camera;
        this.crosshairTarget = crosshairTarget;
    }

    public <A extends Attachable> void render(
        A attachable,
        float tickDelta,
        PoseStack matrices,
        MultiBufferSource vertexConsumers
    ) {
        var attachableRenderer = this.get(attachable);
        if (attachableRenderer != null) {
            if (attachableRenderer.isInRenderDistance(attachable, this.camera.position())) {
                try {
                    render(attachableRenderer, attachable, tickDelta, matrices, vertexConsumers);
                } catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.forThrowable(throwable, "Rendering Attachable");
                    CrashReportCategory crashReportSection = crashReport.addCategory("Attachable Details");
                    crashReportSection.setDetail("Name", ChowlRegistries.ATTACHABLE_TYPE.getKey(attachable.getType()) + " // " + attachable.getClass().getCanonicalName());
                    throw new ReportedException(crashReport);
                }
            }
        }
    }

    public <A extends Attachable> void renderOutline(
        A attachable,
        Camera camera,
        MultiBufferSource.BufferSource vertexConsumers,
        PoseStack matrices
    ) {
        var attachableRenderer = this.get(attachable);
        if (attachableRenderer != null) {
            attachableRenderer.renderOutline(
                attachable,
                camera,
                vertexConsumers,
                matrices
            );
        }
    }

    private static <T extends Attachable> void render(
        AttachableRenderer<T> renderer,
        T attachableState,
        float tickDelta,
        PoseStack matrices,
        MultiBufferSource vertexConsumers
    ) {
//        World world = attachable.getWorld();
//        int i;
//        if (world != null) {
//            i = WorldRenderer.getLightmapCoordinates(world, attachable.getPos());
//        } else {
//            i = 15728880;
//        }

        renderer.render(attachableState, tickDelta, matrices, vertexConsumers, 15728880, OverlayTexture.NO_OVERLAY);
    }

    public void setWorld(@Nullable Level world) {
        this.world = world;
        if (world == null) {
            this.camera = null;
        }

    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        AttachableRendererFactory.Context context = new AttachableRendererFactory.Context(
            this,
            this.itemRenderer,
            this.itemModelManager,
            this.blockRenderManager,
            this.entityRenderManager,
            this.blockEntityRenderManager,
            this.textRenderer
        );
        this.renderers = AttachableRendererFactories.reload(context);
    }
}
