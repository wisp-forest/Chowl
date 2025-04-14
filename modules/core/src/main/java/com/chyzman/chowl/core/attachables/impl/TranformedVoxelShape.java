package com.chyzman.chowl.core.attachables.impl;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.Objects;

public record TranformedVoxelShape(
        VoxelShape shape,
        Quaternionf rotation
) {
    @Nullable
    public Vec3d raycast(Vec3d start, Vec3d end, Vec3d pos) {
        if (this.shape().isEmpty()) return null;
        var realStart = new Vec3d(rotation.transformInverse(start.subtract(pos).toVector3f())).add(pos);
        var realEnd = new Vec3d(rotation.transformInverse(end.subtract(pos).toVector3f())).add(pos);
        var hitPos = this.shape.getBoundingBoxes()
                .stream()
                .map(box -> box.offset(pos).raycast(realStart, realEnd).orElse(null))
                .filter(Objects::nonNull)
                .reduce(realEnd, (best, current) -> current.distanceTo(realStart) < best.distanceTo(realStart) ? current : best);
        if (hitPos.equals(realEnd)) return null;
        return new Vec3d(rotation.transform(hitPos.subtract(pos).toVector3f())).add(pos);
    }

    @Nullable
    public Vec3d getClosestPointTo(Vec3d pos) {
        return this.shape.getClosestPointTo(new Vec3d(rotation.transformInverse(pos.subtract(pos).toVector3f())).add(pos))
                .map(v -> new Vec3d(rotation.transform(v.toVector3f())).add(pos))
                .orElse(null);
    }
}
