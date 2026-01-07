package com.chyzman.chowl.core.multipart.api.client.render;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.multipart.api.client.PartRendererFactories;
import com.chyzman.chowl.core.multipart.api.client.PartRendererFactory;
import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PartRenderDispatcher implements ResourceManagerReloadListener {
    private Map<PartType<?>, PartRenderer<?, ?>> renderers = ImmutableMap.of();
    private final Font font;
    private final Supplier<EntityModelSet> entityModelSet;
    private Vec3 cameraPos;
    private final BlockRenderDispatcher blockRenderDispatcher;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final ItemModelResolver itemModelResolver;
    private final ItemRenderer itemRenderer;
    private final EntityRenderDispatcher entityRenderer;
    private final MaterialSet materials;
    private final PlayerSkinRenderCache playerSkinRenderCache;
    PartRendererFactory.Context context;

    public PartRenderDispatcher(
      Font font,
      Supplier<EntityModelSet> entityModelSet,
      BlockRenderDispatcher blockRenderDispatcher,
      BlockEntityRenderDispatcher blockEntityRenderDispatcher,
      ItemModelResolver itemModelResolver,
      ItemRenderer itemRenderer,
      EntityRenderDispatcher entityRenderer,
      MaterialSet materials,
      PlayerSkinRenderCache playerSkinRenderCache
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
        this.cameraPos = camera.position();
    }

    @Nullable
    public <E extends Part, S extends PartRenderState> S getRenderState(E part, float tickProgress, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        PartRenderer<E, S> partRenderer = this.get(part);
        if (partRenderer == null || !part.hasWorld()) {
            return null;
        }

        Vec3 vec3d = this.cameraPos;
        S blockEntityRenderState = partRenderer.createRenderState();
        partRenderer.extractRenderState(part, blockEntityRenderState, tickProgress, vec3d, crumblingOverlay);
        return blockEntityRenderState;
    }

    @Nullable
    public <E extends Part, S extends PartRenderState> S getBakedRenderState(E part) {
        PartRenderer<E, S> partRenderer = this.get(part);
        if (partRenderer == null || !part.hasWorld()) {
            return null;
        }

        S blockEntityRenderState = partRenderer.createRenderState();
        partRenderer.extractBakingRenderState(part, blockEntityRenderState);
        return blockEntityRenderState;
    }

    public <S extends PartRenderState> void render(S renderState, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraRenderState) {
        PartRenderer<?, S> partRenderer = this.getByRenderState(renderState);
        if (partRenderer != null) {
            try {
                partRenderer.render(renderState, matrices, queue, cameraRenderState);
            } catch (Throwable var9) {
                CrashReport crashReport = CrashReport.forThrowable(var9, "Rendering Part");
                CrashReportCategory crashReportSection = crashReport.addCategory("Part Details");
                renderState.populateCrashReport(crashReportSection);
                throw new ReportedException(crashReport);
            }
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        context = new PartRendererFactory.Context(
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
