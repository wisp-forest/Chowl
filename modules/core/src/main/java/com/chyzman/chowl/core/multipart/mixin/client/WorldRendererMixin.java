package com.chyzman.chowl.core.multipart.mixin.client;

import com.chyzman.chowl.core.block.api.MultipartHolderBlockWithEntity;
import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.multipart.api.MultipartHitResult;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.pond.MinecraftClientDuck;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Shadow private @Nullable ClientLevel level;
    @Shadow @Final private Minecraft minecraft;

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"), method = "extractBlockOutline")
    private VoxelShape multipartOutline(BlockState instance, BlockGetter blockView, BlockPos blockPos, CollisionContext shapeContext, Operation<VoxelShape> original, @Local BlockHitResult hitResult) {
        if (!(hitResult instanceof MultipartHitResult result) || level == null) {
            return original.call(instance, blockView, blockPos, shapeContext);
        }

        BlockEntity blockEntity = level.getBlockEntity(blockPos); // TODO: Not check for MultipartHolderBlockWithEntity but a more open interface instead
        if (!(blockEntity instanceof MultipartHolderBlockEntity multipartHolder) || !(instance.getBlock() instanceof MultipartHolderBlockWithEntity multipartBlock)) {
            return original.call(instance, blockView, blockPos, shapeContext);
        }

        Part part = null;
        List<Part> parts = multipartHolder.getParts();
        List<Part> nextParts = multipartHolder.getParts();
        for (byte partIndex : result.getPart()) {
            // Super advanced desync handling
            if (nextParts.size() <= partIndex) {
                return multipartBlock.getBlockOutlineShape(instance, this.level, blockPos, shapeContext);
            }

            part = nextParts.get(partIndex);
            parts = nextParts;
            nextParts = part.getSubParts();
        }

        if (part != null) {
            return part.getPartOutlineShape(parts, this.level, blockPos, shapeContext);
        }

        return multipartBlock.getBlockOutlineShape(instance, this.level, blockPos, shapeContext);
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;prepare(Lnet/minecraft/client/Camera;)V"))
    private void configurePartRenderDispatcher(
      GraphicsResourceAllocator allocator,
      DeltaTracker tickCounter,
      boolean renderBlockOutline,
      Camera camera,
      Matrix4f positionMatrix,
      Matrix4f basicProjectionMatrix,
      Matrix4f projectionMatrix,
      GpuBufferSlice fogBuffer,
      Vector4f fogColor,
      boolean renderSky,
      CallbackInfo ci
    ) {
        ((MinecraftClientDuck) this.minecraft).chowl$getPartRenderDispatcher().configure(camera);
    }
}
