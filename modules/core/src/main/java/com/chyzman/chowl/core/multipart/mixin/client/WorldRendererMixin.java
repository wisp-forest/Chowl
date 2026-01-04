package com.chyzman.chowl.core.multipart.mixin.client;

import com.chyzman.chowl.core.block.api.MultipartHolderBlockWithEntity;
import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.multipart.api.MultipartHitResult;
import com.chyzman.chowl.core.multipart.api.Part;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow private @Nullable ClientWorld world;
    @Shadow @Final private MinecraftClient client;

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"), method = "fillEntityOutlineRenderStates")
    private VoxelShape multipartOutline(BlockState instance, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext, Operation<VoxelShape> original, @Local BlockHitResult hitResult) {
        if (!(hitResult instanceof MultipartHitResult result) || world == null) {
            return original.call(instance, blockView, blockPos, shapeContext);
        }

        BlockEntity blockEntity = world.getBlockEntity(blockPos); // TODO: Not check for MultipartHolderBlockWithEntity but a more open interface instead
        if (!(blockEntity instanceof MultipartHolderBlockEntity multipartHolder) || !(instance.getBlock() instanceof MultipartHolderBlockWithEntity multipartBlock)) {
            return original.call(instance, blockView, blockPos, shapeContext);
        }

        Part part = null;
        List<Part> parts = multipartHolder.getParts();
        List<Part> nextParts = multipartHolder.getParts();
        for (byte partIndex : result.getPart()) {
            // Super advanced desync handling
            if (nextParts.size() <= partIndex) {
                return multipartBlock.getBlockOutlineShape(instance, this.world, blockPos, shapeContext);
            }

            part = nextParts.get(partIndex);
            parts = nextParts;
            nextParts = part.getSubParts();
        }

        if (part != null) {
            return part.getPartOutlineShape(parts, this.world, blockPos, shapeContext);
        }

        return multipartBlock.getBlockOutlineShape(instance, this.world, blockPos, shapeContext);
    }

    // FIXME: This is also quite cooked
    /*@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;configure(Lnet/minecraft/world/World;Lnet/minecraft/client/render/Camera;Lnet/minecraft/util/hit/HitResult;)V"))
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

    }*/
}
