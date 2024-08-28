package com.chyzman.chowl.industries.block.button;

import com.chyzman.chowl.industries.block.DrawerFrameBlock;
import com.chyzman.chowl.industries.util.BlockSideUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public interface BlockButtonProvider {
    List<BlockButton> listButtons(World world, BlockState state, BlockPos pos, Direction side);

    default @Nullable BlockButton findButton(World world, BlockState state, BlockHitResult hitResult, int orientation) {
        Vector3f vec = hitResult.getPos().subtract(hitResult.getBlockPos().toCenterPos()).toVector3f();
        var side = BlockSideUtils.getSide(hitResult);
        vec.rotate(side.getRotationQuaternion().invert())
                .rotate(Direction.WEST.getRotationQuaternion())
                .rotate(RotationAxis.NEGATIVE_X.rotationDegrees((orientation > 0 && orientation < 4) ? orientation * 90 : 0));

//        if (Math.abs(vec.x) != 0.5) return null;

        vec.add(0.5f, 0.5f, 0.5f);

        for (var button : listButtons(world, state, hitResult.getBlockPos(), side)) {
            if (!button.isIn(vec.z, vec.y)) continue;
            return button;
        }

        return null;
    }

    default @NotNull ActionResult tryUseButtons(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.canInteractWithBlockAt(pos, 0)) return ActionResult.PASS;

        BlockButton button = findButton(world, state, hit, DrawerFrameBlock.getOrientation(world, hit));

        if (button == null) return ActionResult.PASS;
        if (button.use() == null) return ActionResult.PASS;
        return button.use().apply(state, world, pos, player, hit);
    }

    default @NotNull ActionResult tryAttackButtons(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        if (!player.canInteractWithBlockAt(hitResult.getBlockPos(), 0)) return ActionResult.PASS;

        BlockButton button = findButton(world, state, hitResult, DrawerFrameBlock.getOrientation(world, hitResult));
        if (button == null) return ActionResult.PASS;
        if (button.attack() == null) return ActionResult.PASS;
        return button.attack().apply(world, state, hitResult, player);
    }

    default @NotNull ActionResult tryDoubleClickButtons(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        if (!player.canInteractWithBlockAt(hitResult.getBlockPos(), 0)) return ActionResult.PASS;

        BlockButton button = findButton(world, state, hitResult, DrawerFrameBlock.getOrientation(world, hitResult));

        if (button == null) return ActionResult.PASS;
        if (button.doubleClick() == null) return ActionResult.PASS;
        return button.doubleClick().apply(world, state, hitResult, player);
    }

    @FunctionalInterface
    interface UseFunction {
        ActionResult apply(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit);
    }

    @FunctionalInterface
    interface AttackFunction {
        ActionResult apply(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player);
    }

    @FunctionalInterface
    interface DoubleClickFunction {
        ActionResult apply(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player);
    }

}
