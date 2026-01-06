package com.chyzman.chowl.core.multipart.api;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MultipartVoxelShape extends VoxelShape {
    private final VoxelShape internalShape;
    private final List<VoxelShape> shapes;
    private final boolean hasBaseShape;

    public MultipartVoxelShape(List<VoxelShape> shapes) {
        this(shapes, false);
    }

    public MultipartVoxelShape(List<VoxelShape> shapes, boolean hasBaseShape) {
        this(shapes.stream().reduce(Shapes::or).orElse(Shapes.empty()), shapes, hasBaseShape);
    }

    public MultipartVoxelShape(VoxelShape internalShape, List<VoxelShape> shapes, boolean hasBaseShape) {
        super(internalShape.shape);
        this.internalShape = internalShape;
        this.shapes = List.copyOf(shapes);
        this.hasBaseShape = hasBaseShape;
    }

    @Override
    public DoubleList getCoords(Direction.Axis axis) {
        return internalShape.getCoords(axis);
    }

    @Override
    public @Nullable BlockHitResult clip(Vec3 start, Vec3 end, BlockPos pos) {
        byte i = (byte) (hasBaseShape ? -1 : 0);
        BlockHitResult result = null;
        for (VoxelShape shape : shapes) {
            BlockHitResult raycastResult = shape.clip(start, end, pos);
            if (raycastResult != null) {
                end = raycastResult.getLocation();
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
