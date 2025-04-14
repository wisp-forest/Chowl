package com.chyzman.chowl.oddities.infoDisplay.blockEntity.renderer;

import com.chyzman.chowl.oddities.infoDisplay.block.AbstractInfoDisplayBlock;
import com.chyzman.chowl.oddities.infoDisplay.block.WallInfoDisplayBlock;
import com.chyzman.chowl.oddities.infoDisplay.blockEntity.InfoDisplayBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public abstract class InfoDisplayBlockEntityRenderer implements BlockEntityRenderer<InfoDisplayBlockEntity> {

    @Override
    public void render(
            InfoDisplayBlockEntity entity,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        var state = entity.getCachedState();

        matrices.push();

        var facing = state.get(AbstractInfoDisplayBlock.FACING);

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(facing.getRotationQuaternion());
        matrices.translate(0, 0.5, 0);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));

        matrices.translate(0, 0, -1);
        matrices.scale(1 / 16f, 1 / 16f, 1 / 16f);

        matrices.translate(0, 0, 0.01);

        if (state.getBlock() instanceof WallInfoDisplayBlock<?>) {
            matrices.translate(0, 0, 1);
            matrices.scale(1/2f, 1/2f, 1/2f);
        } else {
            matrices.translate(0, -5.5, 10);
            matrices.scale(1/2.75f, 1/2.75f, 1/2.75f);
        }

        matrices.scale(1, -1, 1);

        renderInfo(entity, tickDelta, matrices, vertexConsumers, light, overlay);

        matrices.pop();
    }

    public abstract void renderInfo(
            InfoDisplayBlockEntity entity,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    );
}
