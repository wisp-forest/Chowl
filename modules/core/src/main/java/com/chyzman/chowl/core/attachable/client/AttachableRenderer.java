package com.chyzman.chowl.core.attachable.client;

import com.chyzman.chowl.core.attachable.Attachable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public interface AttachableRenderer<T extends Attachable> {
    void render(T attachable, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);

    default boolean rendersOutsideBoundingBox(T attachable) {
        return false;
    }

    default int getRenderDistance() {
        return 64;
    }

    default boolean isInRenderDistance(T attachable, Vec3d pos) {
        return attachable.getClosestPoint(pos).isInRange(pos, this.getRenderDistance());
    }

}
