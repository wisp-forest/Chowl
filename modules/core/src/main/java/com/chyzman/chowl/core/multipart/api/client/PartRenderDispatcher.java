package com.chyzman.chowl.core.multipart.api.client;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
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

@Environment(EnvType.CLIENT)
public class PartRenderDispatcher implements SynchronousResourceReloader {
    private Map<PartType<?>, PartRenderer<?>> renderers = ImmutableMap.of();
    private final TextRenderer textRenderer;
    private final Supplier<LoadedEntityModels> entityModelsGetter;
    public World world;
    public Camera camera;
    public HitResult crosshairTarget;
    private final BlockRenderManager blockRenderManager;
    private final ItemModelManager itemModelManager;
    private final ItemRenderer itemRenderer;
    private final EntityRenderDispatcher entityRenderDispatcher;

    public PartRenderDispatcher(
      TextRenderer textRenderer,
      Supplier<LoadedEntityModels> entityModelsGetter,
      BlockRenderManager blockRenderManager,
      ItemModelManager itemModelManager,
      ItemRenderer itemRenderer,
      EntityRenderDispatcher entityRenderDispatcher
    ) {
        this.itemRenderer = itemRenderer;
        this.itemModelManager = itemModelManager;
        this.entityRenderDispatcher = entityRenderDispatcher;
        this.textRenderer = textRenderer;
        this.entityModelsGetter = entityModelsGetter;
        this.blockRenderManager = blockRenderManager;
    }

    @Nullable
    public <E extends Part> PartRenderer<E> get(E blockEntity) {
        return (PartRenderer<E>) this.renderers.get(blockEntity.getType());
    }

    public void configure(World world, Camera camera, HitResult crosshairTarget) {
        if (this.world != world) {
            this.setWorld(world);
        }

        this.camera = camera;
        this.crosshairTarget = crosshairTarget;
    }

    public <E extends Part> void render(E part, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        PartRenderer<E> blockEntityRenderer = this.get(part);
        if (blockEntityRenderer != null) {
            if (part.isInitialized()) {
                try {
                    render(blockEntityRenderer, part, tickDelta, matrices, vertexConsumers);
                } catch (Throwable var9) {
                    CrashReport crashReport = CrashReport.create(var9, "Rendering Block Entity");
                    CrashReportSection crashReportSection = crashReport.addElement("Block Entity Details");
                    part.populateCrashReport(crashReportSection);
                    throw new CrashException(crashReport);
                }
            }
        }
    }

    private static <T extends Part> void render(
      PartRenderer<T> renderer, T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers
    ) {
        World world = blockEntity.getWorld();
        int i;
        if (world != null) {
            i = WorldRenderer.getLightmapCoordinates(world, blockEntity.getPos());
        } else {
            i = LightmapTextureManager.MAX_LIGHT_COORDINATE;
        }

        renderer.render(blockEntity, tickDelta, matrices, vertexConsumers, i, OverlayTexture.DEFAULT_UV);
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
        if (world == null) {
            this.camera = null;
        }
    }

    @Override
    public void reload(ResourceManager manager) {
        PartRendererFactory.Context context = new PartRendererFactory.Context(
          this,
          this.blockRenderManager,
          this.itemModelManager,
          this.itemRenderer,
          this.entityRenderDispatcher,
          (LoadedEntityModels) this.entityModelsGetter.get(),
          this.textRenderer
        );
        this.renderers = PartRendererFactories.reload(context);
    }
}
