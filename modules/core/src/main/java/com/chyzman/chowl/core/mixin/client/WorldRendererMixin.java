package com.chyzman.chowl.core.mixin.client;

import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.pond.MultipartHitResult;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow private @Nullable ClientWorld world;

    @WrapOperation(method = "renderTargetBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)V"))
    private void renderTargetMultipartOutline(
      WorldRenderer instance,
      MatrixStack matrices,
      VertexConsumer vertexConsumer,
      Entity entity,
      double cameraX,
      double cameraY,
      double cameraZ,
      BlockPos pos,
      BlockState state,
      int color,
      Operation<Void> original,
      @Local BlockHitResult hitResult
    ) {
        short partIndex = ((MultipartHitResult) hitResult).chowl$getHitMultipart();
        if (partIndex == -1 || world == null) {
            original.call(instance, matrices, vertexConsumer, entity, cameraX, cameraY, cameraZ, pos, state, color);
            return;
        }

        //TODO: store in the hit result
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof MultipartHolderBlockEntity multipartHolder)) {
            original.call(instance, matrices, vertexConsumer, entity, cameraX, cameraY, cameraZ, pos, state, color);
            return;
        }

        List<Part> parts = multipartHolder.getParts();
        drawBlockOutline(matrices, vertexConsumer, entity, cameraX, cameraY, cameraZ, pos, parts, parts.get(partIndex), color);
    }

    @Unique
    private void drawBlockOutline(
      MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ,
      BlockPos pos, List<Part> parts, Part part, int color
    ) {
        VertexRendering.drawOutline(
          matrices,
          vertexConsumer,
          part.getOutlineShape(parts, this.world, pos, ShapeContext.of(entity)),
          pos.getX() - cameraX,
          pos.getY() - cameraY,
          pos.getZ() - cameraZ,
          color
        );
    }
}
