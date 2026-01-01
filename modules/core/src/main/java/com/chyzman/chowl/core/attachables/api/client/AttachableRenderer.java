package com.chyzman.chowl.core.attachables.api.client;

import com.chyzman.chowl.core.attachables.api.Attachable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public interface AttachableRenderer<T extends Attachable> {
    void render(T attachable, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);

    void renderOutline(T attachable, Camera camera, VertexConsumerProvider.Immediate vertexConsumers, MatrixStack matrices);

    default boolean rendersOutsideBoundingBox(T attachable) {
        return false;
    }

    default int getRenderDistance() {
        return 64;
    }

    default boolean isInRenderDistance(T attachable, Vec3d pos) {
        return attachable.getClosestPointTo(pos).isInRange(pos, this.getRenderDistance());
    }

    static void drawDebugVector(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d pos, Quaternionf rotation) {
        if (!MinecraftClient.getInstance().getEntityRenderDispatcher().shouldRenderHitboxes()) return;
        EntityRenderDispatcher.drawVector(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getLines()),
                pos.toVector3f(),
                new Vec3d(rotation.transform(new Vector3f(0, 0.01f, 0))),
                -16776961
        );
    }

    static void drawDebugBoundingBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d pos, Quaternionf rotation, Box shape) {
        if (!MinecraftClient.getInstance().getEntityRenderDispatcher().shouldRenderHitboxes()) return;
        matrices.push();
        matrices.translate(pos.x, pos.y, pos.z);
        matrices.multiply(rotation);
        WorldRenderer.drawBox(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getLines()),
                shape,
                1.0f,
                1.0f,
                1.0f,
                1.0f
        );
        matrices.pop();
    }
}
