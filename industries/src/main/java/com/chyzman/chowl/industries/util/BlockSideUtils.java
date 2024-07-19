package com.chyzman.chowl.industries.util;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Comparator;

public final class BlockSideUtils {
    private static final Direction[] DIRECTIONS = Direction.values();

    private BlockSideUtils() {

    }


    public static Direction getSide(BlockHitResult hitResult) {
        return Arrays.stream(DIRECTIONS).min(Comparator.comparingDouble(
                i -> Vec3d.of(i.getVector()).squaredDistanceTo(
                        hitResult.getPos().subtract(hitResult.getBlockPos().toCenterPos())
                ))).orElse(hitResult.getSide());
    }
}
