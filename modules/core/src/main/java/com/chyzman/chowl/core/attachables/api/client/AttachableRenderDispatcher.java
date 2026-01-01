package com.chyzman.chowl.core.attachables.api.client;

import com.chyzman.chowl.core.attachables.api.Attachable;
import com.chyzman.chowl.core.attachables.api.AttachableType;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class AttachableRenderDispatcher implements SynchronousResourceReloader {
    private Map<AttachableType<?>, AttachableRenderer<?>> renderers = ImmutableMap.of();

    public World world;
    public Camera camera;
    public HitResult crosshairTarget;

    private final ItemRenderer itemRenderer;
    private final ItemModelManager itemModelManager;
    private final BlockRenderManager blockRenderManager;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final TextRenderer textRenderer;

    public AttachableRenderDispatcher(
            ItemRenderer itemRenderer,
            ItemModelManager itemModelManager,
            BlockRenderManager blockRenderManager,
            BlockEntityRenderDispatcher blockEntityRenderDispatcher,
            EntityRenderDispatcher entityRenderDispatcher,
            TextRenderer textRenderer
    ) {
        this.itemRenderer = itemRenderer;
        this.itemModelManager = itemModelManager;
        this.blockRenderManager = blockRenderManager;
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
        this.entityRenderDispatcher = entityRenderDispatcher;
        this.textRenderer = textRenderer;
    }

    @Nullable
    public <A extends Attachable> AttachableRenderer<A> get(A attachableState) {
        return (AttachableRenderer<A>) this.renderers.get(attachableState.getType());
    }

    public void configure(World world, Camera camera, HitResult crosshairTarget) {
        if (this.world != world) {
            this.setWorld(world);
        }

        this.camera = camera;
        this.crosshairTarget = crosshairTarget;
    }

    public <A extends Attachable> void render(
            A attachable,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers
    ) {
        var attachableRenderer = this.get(attachable);
        if (attachableRenderer != null) {
            if (attachableRenderer.isInRenderDistance(attachable, this.camera.getPos())) {
                try {
                    render(attachableRenderer, attachable, tickDelta, matrices, vertexConsumers);
                } catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.create(throwable, "Rendering Attachable");
                    CrashReportSection crashReportSection = crashReport.addElement("Attachable Details");
                    crashReportSection.add("Name", ChowlRegistries.ATTACHABLE_TYPE.getId(attachable.getType()) + " // " + attachable.getClass().getCanonicalName());
                    throw new CrashException(crashReport);
                }
            }
        }
    }

    public <A extends Attachable> void renderOutline(
            A attachable,
            Camera camera,
            VertexConsumerProvider.Immediate vertexConsumers,
            MatrixStack matrices
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
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers
    ) {
//        World world = attachable.getWorld();
//        int i;
//        if (world != null) {
//            i = WorldRenderer.getLightmapCoordinates(world, attachable.getPos());
//        } else {
//            i = 15728880;
//        }

        renderer.render(attachableState, tickDelta, matrices, vertexConsumers, 15728880, OverlayTexture.DEFAULT_UV);
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
        if (world == null) {
            this.camera = null;
        }

    }

    @Override
    public void reload(ResourceManager manager) {
        AttachableRendererFactory.Context context = new AttachableRendererFactory.Context(
                this,
                this.itemRenderer,
                this.itemModelManager,
                this.blockRenderManager,
                this.entityRenderDispatcher,
                this.blockEntityRenderDispatcher,
                this.textRenderer
        );
        this.renderers = AttachableRendererFactories.reload(context);
    }
}
