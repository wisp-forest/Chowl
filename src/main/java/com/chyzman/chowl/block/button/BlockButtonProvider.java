package com.chyzman.chowl.block.button;

import com.chyzman.chowl.block.DoubleClickableBlock;
import com.chyzman.chowl.block.DrawerFrameBlock;
import com.chyzman.chowl.classes.AttackInteractionReceiver;
import com.chyzman.chowl.util.BlockSideUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public interface BlockButtonProvider extends AttackInteractionReceiver, DoubleClickableBlock {
    List<BlockButton> listButtons(World world, BlockState state, BlockPos pos, Direction side);

    default @Nullable BlockButton findButton(World world, BlockState state, BlockHitResult hitResult, int orientation) {
        Vector3f vec = hitResult.getPos().subtract(hitResult.getBlockPos().toCenterPos()).toVector3f();
        var side = BlockSideUtils.getSide(hitResult);
        vec.rotate(side.getRotationQuaternion().invert())
                .rotate(Direction.WEST.getRotationQuaternion())
                .rotate(RotationAxis.NEGATIVE_X.rotationDegrees(orientation * 90));

        vec.add(0.5f, 0.5f, 0.5f);

        for (var button : listButtons(world, state, hitResult.getBlockPos(), side)) {
            if (!button.isIn(vec.z, vec.y)) continue;
            return button;
        }

        return null;
    }

    default @NotNull ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockButton button = findButton(world, state, hit, DrawerFrameBlock.getOrientation(world, hit));

        if (button == null) return ActionResult.PASS;
        if (button.use() == null) return ActionResult.PASS;
        return button.use().apply(state, world, pos, player, hand, hit);
    }

    @Override
    default @NotNull ActionResult onAttack(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        BlockButton button = findButton(world, state, hitResult, DrawerFrameBlock.getOrientation(world, hitResult));
        if (button == null) return ActionResult.PASS;
        if (button.attack() == null) return ActionResult.PASS;
        return button.attack().apply(world, state, hitResult, player);
    }

    @Override
    default @NotNull ActionResult onDoubleClick(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        BlockButton button = findButton(world, state, hitResult, DrawerFrameBlock.getOrientation(world, hitResult));

        if (button == null) return ActionResult.PASS;
        if (button.doubleClick() == null) return ActionResult.PASS;
        return button.doubleClick().apply(world, state, hitResult, player);
    }

    @FunctionalInterface
    interface UseFunction {
        ActionResult apply(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit);
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