package com.chyzman.chowl.core.attachables.api.client;

import com.chyzman.chowl.core.attachables.api.Attachable;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public interface AttachableRenderer<T extends Attachable> {
    void render(T attachable, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay);

    void renderOutline(T attachable, Camera camera, MultiBufferSource.BufferSource vertexConsumers, PoseStack matrices);

    default boolean rendersOutsideBoundingBox(T attachable) {
        return false;
    }

    default int getRenderDistance() {
        return 64;
    }

    default boolean isInRenderDistance(T attachable, Vec3 pos) {
        return attachable.getClosestPointTo(pos).closerThan(pos, this.getRenderDistance());
    }

    static void drawDebugVector(PoseStack matrices, MultiBufferSource vertexConsumers, Vec3 pos, Quaternionf rotation) {
        // FIXME: I'm too tired to figure this one out today
        /*if (!MinecraftClient.getInstance().getEntityRenderDispatcher().shouldRenderHitboxes()) return;
        VertexRendering.drawVector(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getLines()),
                pos.toVector3f(),
                new Vec3d(rotation.transform(new Vector3f(0, 0.01f, 0))),
                -16776961
        );*/
    }

    static void drawDebugBoundingBox(PoseStack matrices, MultiBufferSource vertexConsumers, Vec3 pos, Quaternionf rotation, AABB shape) {
        // FIXME: I'm too tired to figure this one out today
        /*if (!MinecraftClient.getInstance().getEntityRenderDispatcher().shouldRenderHitboxes()) return;
        matrices.push();
        matrices.translate(pos);
        matrices.multiply(rotation);
        VertexRendering.drawBox(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getLines()),
                shape,
                1.0f,
                1.0f,
                1.0f,
                1.0f
        );
        matrices.pop();*/
    }
}
