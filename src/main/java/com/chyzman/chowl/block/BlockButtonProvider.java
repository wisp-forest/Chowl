package com.chyzman.chowl.block;

import com.chyzman.chowl.classes.AttackInteractionReceiver;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public interface BlockButtonProvider extends AttackInteractionReceiver {
    List<Button> listButtons(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player);
    ActionResult attackButton(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player, Button button);

    default @Nullable Button findButton(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        Vector3f vec = hitResult.getPos().subtract(hitResult.getBlockPos().toCenterPos()).toVector3f();
        vec.rotate(hitResult.getSide().getRotationQuaternion().invert()).rotate(Direction.WEST.getRotationQuaternion());
        vec.add(0.5f, 0.5f, 0.5f);

        for (var button : listButtons(world, state, hitResult, player)) {
            if (!button.isIn(vec.y, vec.z)) continue;

            return button;
        }

        return null;
    }

    @Override
    default @NotNull ActionResult onAttack(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        Button button = findButton(world, state, hitResult, player);

        if (button == null) return ActionResult.PASS;
        return attackButton(world, state, hitResult, player, button);
    }

    record Button(float minX, float minY, float maxX, float maxY, boolean canAttack) {
        public boolean isIn(float x, float y) {
            return minX <= x && x <= maxX && minY <= y && y <= maxY;
        }
    }
}
