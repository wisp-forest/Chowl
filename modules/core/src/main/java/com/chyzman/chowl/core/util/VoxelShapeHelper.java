package com.chyzman.chowl.core.util;

import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.util.Util;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.List;
import java.util.Map;

public class VoxelShapeHelper {
    public static VoxelShape rotate(VoxelShape shape, Direction.Axis axis, int amount) {
        List<AABB> boxes = shape.toAabbs();

        VoxelShape newShape = Shapes.empty();

        for (AABB box : boxes) {
            Vec3 minPos = rotateVec3d(box.getMinPosition(), axis, amount);
            Vec3 maxPos = rotateVec3d(box.getMaxPosition(), axis, amount);
            newShape = Shapes.or(newShape, Shapes.create(new AABB(minPos, maxPos)));
        }

        return newShape.optimize();
    }

    private static Vec3 rotateVec3d(Vec3 pos, Direction.Axis axis, int amount) {
        Vec3 newPos = pos.subtract(0.5, 0.5, 0.5);
        return (switch (axis) {
            case X -> newPos.xRot(amount * (float) (Math.PI / 2));
            case Y -> newPos.yRot(amount * (float) (Math.PI / 2));
            case Z -> newPos.zRot(amount * (float) (Math.PI / 2));
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

    public static VoxelShape rotate(VoxelShape northUpShape, FrontAndTop orientation) {
        var facing = rotate(northUpShape, orientation.front());
        if (orientation.top() == Direction.UP) return facing;
        return rotate(facing ,orientation.top());
    }
}
