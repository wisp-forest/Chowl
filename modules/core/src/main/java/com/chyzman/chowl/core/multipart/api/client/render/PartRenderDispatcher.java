package com.chyzman.chowl.core.multipart.api.client.render;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.multipart.api.client.PartRendererFactories;
import com.chyzman.chowl.core.multipart.api.client.PartRendererFactory;
import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PartRenderDispatcher implements SynchronousResourceReloader {
    private Map<PartType<?>, PartRenderer<?, ?>> renderers = ImmutableMap.of();
    private final TextRenderer font;
    private final Supplier<LoadedEntityModels> entityModelSet;
    private Vec3d cameraPos;
    private final BlockRenderManager blockRenderDispatcher;
    private final BlockEntityRenderManager blockEntityRenderDispatcher;
    private final ItemModelManager itemModelResolver;
    private final ItemRenderer itemRenderer;
    private final EntityRenderManager entityRenderer;
    private final SpriteHolder materials;
    private final PlayerSkinCache playerSkinRenderCache;

    public PartRenderDispatcher(
      TextRenderer font,
      Supplier<LoadedEntityModels> entityModelSet,
      BlockRenderManager blockRenderDispatcher,
      BlockEntityRenderManager blockEntityRenderDispatcher,
      ItemModelManager itemModelResolver,
      ItemRenderer itemRenderer,
      EntityRenderManager entityRenderer,
      SpriteHolder materials,
      PlayerSkinCache playerSkinRenderCache
    ) {
        this.font = font;
        this.entityModelSet = entityModelSet;
        this.blockRenderDispatcher = blockRenderDispatcher;
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
        this.itemModelResolver = itemModelResolver;
        this.itemRenderer = itemRenderer;
        this.entityRenderer = entityRenderer;
        this.materials = materials;
        this.playerSkinRenderCache = playerSkinRenderCache;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <E extends Part, S extends PartRenderState> PartRenderer<E, S> get(E part) {
        return (PartRenderer<E, S>) this.renderers.get(part.getType());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <E extends Part, S extends PartRenderState> PartRenderer<E, S> getByRenderState(S renderState) {
        return (PartRenderer<E, S>) this.renderers.get(renderState.type);
    }

    public void configure(Camera camera) {
        this.cameraPos = camera.getCameraPos();
    }

    @Nullable
    public <E extends Part, S extends PartRenderState> S getRenderState(E part, float tickProgress, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        PartRenderer<E, S> partRenderer = this.get(part);
        if (partRenderer == null || !part.hasWorld()) {
            return null;
        }

        Vec3d vec3d = this.cameraPos;
        S blockEntityRenderState = partRenderer.createRenderState();
        partRenderer.updateRenderState(part, blockEntityRenderState, tickProgress, vec3d, crumblingOverlay);
        return blockEntityRenderState;
    }

    @Nullable
    public <E extends Part, S extends PartRenderState> S getBakedRenderState(E part) {
        PartRenderer<E, S> partRenderer = this.get(part);
        if (partRenderer == null || !part.hasWorld()) {
            return null;
        }

        S blockEntityRenderState = partRenderer.createRenderState();
        partRenderer.updateBakedRenderState(part, blockEntityRenderState);
        return blockEntityRenderState;
    }

    public <S extends PartRenderState> void render(S renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        PartRenderer<?, S> partRenderer = this.getByRenderState(renderState);
        if (partRenderer != null) {
            try {
                partRenderer.render(renderState, matrices, queue, cameraRenderState);
            } catch (Throwable var9) {
                CrashReport crashReport = CrashReport.create(var9, "Rendering Part");
                CrashReportSection crashReportSection = crashReport.addElement("Part Details");
                renderState.populateCrashReport(crashReportSection);
                throw new CrashException(crashReport);
            }
        }
    }

    @Override
    public void reload(ResourceManager manager) {
        PartRendererFactory.Context context = new PartRendererFactory.Context(
          this,
          this.blockRenderDispatcher,
          this.blockEntityRenderDispatcher,
          this.itemModelResolver,
          this.itemRenderer,
          this.entityRenderer,
          this.entityModelSet.get(),
          this.font,
          this.materials,
          this.playerSkinRenderCache
        );

        this.renderers = PartRendererFactories.reload(context);
    }
}
