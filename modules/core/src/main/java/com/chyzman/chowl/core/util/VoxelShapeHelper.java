package com.chyzman.chowl.core.util;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.enums.Orientation;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.List;
import java.util.Map;

public class VoxelShapeHelper {
    public static VoxelShape rotate(VoxelShape shape, Direction.Axis axis, int amount) {
        List<Box> boxes = shape.getBoundingBoxes();

        VoxelShape newShape = VoxelShapes.empty();

        for (Box box : boxes) {
            Vec3d minPos = rotateVec3d(box.getMinPos(), axis, amount);
            Vec3d maxPos = rotateVec3d(box.getMaxPos(), axis, amount);
            newShape = VoxelShapes.union(newShape, VoxelShapes.cuboid(new Box(minPos, maxPos)));
        }

        return newShape.simplify();
    }

    private static Vec3d rotateVec3d(Vec3d pos, Direction.Axis axis, int amount) {
        Vec3d newPos = pos.subtract(0.5, 0.5, 0.5);
        return (switch (axis) {
            case X -> newPos.rotateX(amount * (float) (Math.PI / 2));
            case Y -> newPos.rotateY(amount * (float) (Math.PI / 2));
            case Z -> newPos.rotateZ(amount * (float) (Math.PI / 2));
        }).add(0.5, 0.5, 0.5);
    }

    public static VoxelShape rotate(VoxelShape northShape, Direction direction) {
        return switch (direction) {
            case NORTH -> northShape;
            case SOUTH -> rotate(northShape, Direction.Axis.Y, 2);
            case WEST -> rotate(northShape, Direction.Axis.Y, 1);
            case EAST -> rotate(northShape, Direction.Axis.Y, -1);
            case DOWN -> rotate(northShape, Direction.Axis.X, 1);
            case UP -> rotate(northShape, Direction.Axis.X, -1);
            default -> throw new IllegalArgumentException("Invalid direction: " + direction);
        };
    }

    public static VoxelShape rotate(VoxelShape northUpShape, Orientation orientation) {
        var facing = rotate(northUpShape, orientation.getFacing());
        if (orientation.getRotation() == Direction.UP) return facing;
        return rotate(facing ,orientation.getRotation());
    }
}
