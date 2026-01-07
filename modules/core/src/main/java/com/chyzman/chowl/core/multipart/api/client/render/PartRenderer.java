package com.chyzman.chowl.core.multipart.api.client.render;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PartRenderer<T extends Part, S extends PartRenderState> {
    @NotNull S createRenderState();

    default void extractRenderState(@NotNull T part, @NotNull S state, float tickProgress, @NotNull Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        PartRenderState.updatePartRenderState(part, state, crumblingOverlay);
    }

    default void extractBakingRenderState(@NotNull T part, @NotNull S state) {
        PartRenderState.updatePartRenderState(part, state, null);
    }

    @ApiStatus.Internal
    default void enteredRenderCycle(@NotNull T part, float tickProgress, @NotNull Vec3 cameraPos) {
        PartRenderManager.activateRegion(part.getPos());

        if (shouldRebake(part, tickProgress, cameraPos)) {
            PartRenderManager.markForRebuild(part.getPos());
        }
    }

    /**
     * Handles invalidation and passing of rendered vertices to the baking system.
     * Override {@link #submitForBaking(PartRenderState, PoseStack, SubmitNodeCollector)} and
     * {@link #submitForRendering(PartRenderState, PoseStack, SubmitNodeCollector, CameraRenderState)} instead of this method.
     */
    @ApiStatus.Internal
    @ApiStatus.NonExtendable
    default void render(S renderState, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraRenderState) {
        submitForRendering(renderState, matrices, queue, cameraRenderState);
    }

    /**
     * Render vertices to be baked into the render region. This method will be called every time the render region is rebuilt - so
     * you should only render vertices that don't move here. You can call {@link PartRenderManager#markForRebuild(BlockPos)} to
     * cause the render region to be rebuilt, but do not call this too frequently as it will affect performance.
     * You must use the provided VertexConsumerProvider and MatrixStack to render your vertices - any use of Tessellator
     * or RenderSystem here will not work. If you need custom rendering settings, you can use a custom RenderLayer.
     */
    void submitForBaking(S renderState, PoseStack matrices, SubmitNodeCollector queue);

    /**
     * Render vertices immediately. This works exactly the same way as a normal BER render method, and can be used for dynamic
     * rendering that changes every frame. In this method you can also check for render invalidation and call {@link PartRenderManager#markForRebuild(BlockPos)}
     * as appropriate.
     */
    void submitForRendering(S renderState, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraRenderState);

    /**
     * Defines if the part should be baked. This is only checked when baking is required.
     *
     * @param part         The part being checked
     * @param tickProgress The current tick progress
     * @param cameraPos    The current camera position
     * @return if part should be baked
     */
    boolean shouldBake(T part, float tickProgress, Vec3 cameraPos);

    /**
     * Defines whenever the part should re-bake.<br/>
     * <b>Rebaking invalidates all parts within the baking region!</b> Avoid rebaking constantly
     *
     * @param part         The part being checked
     * @param tickProgress The current tick progress
     * @param cameraPos    The current camera position
     * @return if the part should be rebaked
     */
    default boolean shouldRebake(T part, float tickProgress, Vec3 cameraPos) {
        return false;
    };
}
