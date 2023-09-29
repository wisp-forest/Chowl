package com.chyzman.chowl.block;

import com.chyzman.chowl.classes.AttackInteractionReceiver;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function6;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface BlockButtonProvider extends AttackInteractionReceiver {
    List<Button> listButtons(World world, BlockState state, BlockHitResult hitResult);

    default @Nullable Button findButton(World world, BlockState state, BlockHitResult hitResult) {
        Vector3f vec = hitResult.getPos().subtract(hitResult.getBlockPos().toCenterPos()).toVector3f();
        vec.rotate(hitResult.getSide().getRotationQuaternion().invert()).rotate(Direction.WEST.getRotationQuaternion());
        vec.add(0.5f, 0.5f, 0.5f);

        for (var button : listButtons(world, state, hitResult)) {
            if (!button.isIn(vec.y, vec.z)) continue;

            return button;
        }

        return null;
    }

    default @NotNull ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Button button = findButton(world, state, hit);

        if (button == null) return ActionResult.PASS;
        if (button.use == null) return ActionResult.PASS;
        return button.use.apply(state, world, pos, player, hand, hit);
    }

    @Override
    default @NotNull ActionResult onAttack(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        Button button = findButton(world, state, hitResult);

        if (button == null) return ActionResult.PASS;
        if (button.attack == null) return ActionResult.PASS;
        return button.attack.apply(world, state, hitResult, player);
    }

    record Button(
            float minX, float minY, float maxX, float maxY, UseFunction use, AttackFunction attack, RenderConsumer render) {
        public boolean isIn(float x, float y) {
            return minX <= x && x <= maxX && minY <= y && y <= maxY;
        }
    }

    @FunctionalInterface
    public interface UseFunction {
        ActionResult apply(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit);
    }

    @FunctionalInterface
    public interface AttackFunction {
        ActionResult apply(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player);
    }

    @FunctionalInterface
    public interface RenderConsumer {
        void consume(MinecraftClient client, DrawerFrameBlockEntity entity, VertexConsumerProvider vertexConsumers, MatrixStack matrices);
    }
}