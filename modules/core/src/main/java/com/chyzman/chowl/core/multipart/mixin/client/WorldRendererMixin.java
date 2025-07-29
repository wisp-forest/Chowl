package com.chyzman.chowl.core.multipart.mixin.client;

import com.chyzman.chowl.core.block.api.MultipartHolderBlockWithEntity;
import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.multipart.api.MultipartHitResult;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.pond.MinecraftClientDuck;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow private @Nullable ClientWorld world;
    @Shadow @Final private MinecraftClient client;

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
        if (!(hitResult instanceof MultipartHitResult result) || world == null) {
            original.call(instance, matrices, vertexConsumer, entity, cameraX, cameraY, cameraZ, pos, state, color);
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos); // TODO: Not check for MultipartHolderBlockWithEntity but a more open interface instead
        if (!(blockEntity instanceof MultipartHolderBlockEntity multipartHolder) || !(state.getBlock() instanceof MultipartHolderBlockWithEntity multipartBlock)) {
            original.call(instance, matrices, vertexConsumer, entity, cameraX, cameraY, cameraZ, pos, state, color);
            return;
        }

        Part part = null;
        List<Part> parts = multipartHolder.getParts();
        List<Part> nextParts = multipartHolder.getParts();
        for (byte partIndex : result.getPart()) {
            if (nextParts.size() <= partIndex) return; // Super advanced desync handling

            part = nextParts.get(partIndex);
            parts = nextParts;
            nextParts = part.getSubParts();
        }

        VoxelShape shape;
        if (part != null) {
            shape = part.getPartOutlineShape(parts, this.world, pos, ShapeContext.of(entity));
        } else {
            shape = multipartBlock.getBlockOutlineShape(state, this.world, pos, ShapeContext.of(entity));
        }

        VertexRendering.drawOutline(
          matrices,
          vertexConsumer,
          shape,
          pos.getX() - cameraX,
          pos.getY() - cameraY,
          pos.getZ() - cameraZ,
          color
        );
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;configure(Lnet/minecraft/world/World;Lnet/minecraft/client/render/Camera;Lnet/minecraft/util/hit/HitResult;)V"))
    private void configurePartRenderDispatcher(
      ObjectAllocator allocator,
      RenderTickCounter tickCounter,
      boolean renderBlockOutline,
      Camera camera,
      GameRenderer gameRenderer,
      Matrix4f positionMatrix,
      Matrix4f projectionMatrix,
      CallbackInfo ci
    ) {
        ((MinecraftClientDuck) this.client).chowl$getPartRenderDispatcher().configure(
          this.client.world,
          camera,
          this.client.crosshairTarget
        );

    }
}
