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
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface BlockButtonProvider extends AttackInteractionReceiver, DoubleClickableBlock {
    List<Button> listButtons(World world, BlockState state, BlockHitResult hitResult);

    default @Nullable Button findButton(World world, BlockState state, BlockHitResult hitResult, int orientation) {
        Vector3f vec = hitResult.getPos().subtract(hitResult.getBlockPos().toCenterPos()).toVector3f();
        var side = DrawerFrameBlock.getSide(hitResult);
        vec.rotate(side.getRotationQuaternion().invert())
                .rotate(Direction.WEST.getRotationQuaternion())
                .rotate(RotationAxis.NEGATIVE_X.rotationDegrees(orientation * 90));

        vec.add(0.5f, 0.5f, 0.5f);

        for (var button : listButtons(world, state, hitResult)) {
            if (!button.isIn(vec.z, vec.y)) continue;
            return button;
        }

        return null;
    }

    default @NotNull ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Button button = findButton(world, state, hit, DrawerFrameBlock.getOrientation(world, hit));

        if (button == null) return ActionResult.PASS;
        if (button.use == null) return ActionResult.PASS;
        return button.use.apply(state, world, pos, player, hand, hit);
    }

    @Override
    default @NotNull ActionResult onAttack(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        Button button = findButton(world, state, hitResult, DrawerFrameBlock.getOrientation(world, hitResult));
        if (button == null) return ActionResult.PASS;
        if (button.attack == null) return ActionResult.PASS;
        return button.attack.apply(world, state, hitResult, player);
    }

    @Override
    default @NotNull ActionResult onDoubleClick(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        Button button = findButton(world, state, hitResult, DrawerFrameBlock.getOrientation(world, hitResult));

        if (button == null) return ActionResult.PASS;
        if (button.doubleClick == null) return ActionResult.PASS;
        return button.doubleClick.apply(world, state, hitResult, player);
    }

    record Button(float minX, float minY, float maxX, float maxY, UseFunction use, AttackFunction attack,
                  DoubleClickFunction doubleClick, RenderConsumer render) {
        public boolean isIn(float x, float y) {
            return minX <= x * 16 && x * 16 <= maxX && minY <= y * 16 && y * 16 <= maxY;
        }

        public boolean equals(Button button) {
            if (button == null) return false;
            return button.minX == minX && button.minY == minY && button.maxX == maxX && button.maxY == maxY;
        }
    }

    class ButtonBuilder {
        private Vector2f min = new Vector2f();
        private Vector2f max = new Vector2f();

        private UseFunction use;
        private AttackFunction attack;
        private DoubleClickFunction doubleClick;
        private RenderConsumer render;

        public ButtonBuilder(float minX, float minY, float maxX, float maxY) {
            this.min.x = minX;
            this.min.y = minY;
            this.max.x = maxX;
            this.max.y = maxY;
        }

        public ButtonBuilder onUse(UseFunction use) {
            this.use = use;
            return this;
        }

        public ButtonBuilder onAttack(AttackFunction attack) {
            this.attack = attack;
            return this;
        }

        public ButtonBuilder onDoubleClick(DoubleClickFunction doubleClick) {
            this.doubleClick = doubleClick;
            return this;
        }

        public ButtonBuilder onRender(RenderConsumer render) {
            this.render = render;
            return this;
        }

        public Button build() {
            return new Button(min.x, min.y, max.x, max.y, use, attack, doubleClick, render);
        }
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

    @FunctionalInterface
    interface RenderConsumer {
        void consume(MinecraftClient client, DrawerFrameBlockEntity entity, BlockHitResult hitResult, VertexConsumerProvider vertexConsumers, MatrixStack matrices, int light, int overlay, boolean hovered);
    }
}