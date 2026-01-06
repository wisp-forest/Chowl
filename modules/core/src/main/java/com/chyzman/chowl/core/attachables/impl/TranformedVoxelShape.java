package com.chyzman.chowl.core.attachables.impl;

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.Objects;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public record TranformedVoxelShape(
        VoxelShape shape,
        Quaternionf rotation
) {
    @Nullable
    public Vec3 raycast(Vec3 start, Vec3 end, Vec3 pos) {
        if (this.shape().isEmpty()) return null;
        var realStart = new Vec3(rotation.transformInverse(start.subtract(pos).toVector3f())).add(pos);
        var realEnd = new Vec3(rotation.transformInverse(end.subtract(pos).toVector3f())).add(pos);
        var hitPos = this.shape.toAabbs()
                .stream()
                .map(box -> box.move(pos).clip(realStart, realEnd).orElse(null))
                .filter(Objects::nonNull)
                .reduce(realEnd, (best, current) -> current.distanceTo(realStart) < best.distanceTo(realStart) ? current : best);
        if (hitPos.equals(realEnd)) return null;
        return new Vec3(rotation.transform(hitPos.subtract(pos).toVector3f())).add(pos);
    }

    @Nullable
    public Vec3 getClosestPointTo(Vec3 pos) {
        return this.shape.closestPointTo(new Vec3(rotation.transformInverse(pos.subtract(pos).toVector3f())).add(pos))
                .map(v -> new Vec3(rotation.transform(v.toVector3f())).add(pos))
                .orElse(null);
    }
}
