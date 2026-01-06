package com.chyzman.chowl.core.attachables.mixin.client;

import com.chyzman.chowl.core.attachables.pond.MinecraftClientDuck;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;prepare(Lnet/minecraft/client/Camera;)V"))
    private void configureAttachableRenderDispatcher(
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
        ((MinecraftClientDuck) this.minecraft).chowl$getAttachableRenderDispatcher().configure(
          this.minecraft.level,
          camera,
          this.minecraft.hitResult
        );
    }

    /*@Inject(method = "setWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;setWorld(Lnet/minecraft/world/World;)V"))
    private void setAttachableRenderDispatcherWorld(
            ClientWorld world, CallbackInfo ci
    ) {
        ((MinecraftClientDuck) this.client).chowl$getAttachableRenderDispatcher().setWorld(world);
    }*/

    /*@Inject(method = "renderTargetBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/BlockHitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"), cancellable = true)
    private void renderTargetAttachableOutline(
            Camera camera,
            VertexConsumerProvider.Immediate vertexConsumers,
            MatrixStack matrices,
            boolean translucent,
            CallbackInfo ci,
            @Local BlockHitResult hitResult
    ) {
        var targetAttachable = ((HitResultDuck) hitResult).chowl$getHitAttachable();
        if (targetAttachable == null) return;

        var dispatcher = ((MinecraftClientDuck) client).chowl$getAttachableRenderDispatcher();

//        world.getAttachedOrCreate(AttachableHolder.TYPE).attachables.forEach((uuid, container) -> {
//            dispatcher.renderOutline(
//                    container.getContained(),
//                    camera,
//                    vertexConsumers,
//                    matrices
//            );
//        });

        dispatcher.renderOutline(
                targetAttachable.getContained(),
                camera,
                vertexConsumers,
                matrices
        );

        ci.cancel();

    }*/
}
