package com.chyzman.chowl.core.util;

import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Comparator;

public final class BlockSideUtils {
    private BlockSideUtils() {}

    public static Direction getSide(BlockHitResult hit) {
        return Direction.stream().min(Comparator.comparingDouble(
            i -> i.getUnitVec3().distanceToSqr(
                hit.getLocation().subtract(hit.getBlockPos().getCenter())
            ))).orElse(hit.getDirection());
    }

    public static FrontAndTop getOrientation(BlockHitResult hit, Player player) {
        var front = getSide(hit);
        var top = Direction.UP;

        if (front.getAxis() == Direction.Axis.Y) top = player.getDirection().getOpposite();

        return FrontAndTop.fromFrontAndTop(front, top);
    }
}
