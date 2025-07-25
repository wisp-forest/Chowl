package com.chyzman.chowl.core.multipart.api;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MultipartVoxelShape extends VoxelShape {
    private final VoxelShape internalShape;
    private final List<VoxelShape> shapes;
    private final boolean hasBaseShape;

    public MultipartVoxelShape(List<VoxelShape> shapes) {
        this(shapes, false);
    }

    public MultipartVoxelShape(List<VoxelShape> shapes, boolean hasBaseShape) {
        this(shapes.stream().reduce(VoxelShapes::union).orElse(VoxelShapes.empty()), shapes, hasBaseShape);
    }

    public MultipartVoxelShape(VoxelShape internalShape, List<VoxelShape> shapes, boolean hasBaseShape) {
        super(internalShape.voxels);
        this.internalShape = internalShape;
        this.shapes = List.copyOf(shapes);
        this.hasBaseShape = hasBaseShape;
    }

    @Override
    public DoubleList getPointPositions(Direction.Axis axis) {
        return internalShape.getPointPositions(axis);
    }

    @Override
    public @Nullable BlockHitResult raycast(Vec3d start, Vec3d end, BlockPos pos) {
        byte i = (byte) (hasBaseShape ? -1 : 0);
        BlockHitResult result = null;
        for (VoxelShape shape : shapes) {
            BlockHitResult raycastResult = shape.raycast(start, end, pos);
            if (raycastResult != null) {
                end = raycastResult.getPos();
            }

            byte increment = (byte) (i > -1 ? 1 : 0);

            byte[] partIndex = new byte[increment];
            if (raycastResult instanceof MultipartHitResult multipartHitResult) {
                byte[] parts = multipartHitResult.getPart();
                partIndex = Arrays.copyOf(parts, parts.length + increment);
            }

            if (i > -1) {
                partIndex[partIndex.length - 1] = i;
                ArrayUtils.reverse(partIndex);
            }

            if (raycastResult != null) {
                result = new MultipartHitResult(raycastResult, partIndex);
            }

            i++;
        }

        return result;
    }
}
