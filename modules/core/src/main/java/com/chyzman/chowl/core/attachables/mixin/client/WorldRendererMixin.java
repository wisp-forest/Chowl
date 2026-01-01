package com.chyzman.chowl.core.attachables.mixin.client;

import com.chyzman.chowl.core.attachables.pond.HitResultDuck;
import com.chyzman.chowl.core.attachables.pond.MinecraftClientDuck;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;configure(Lnet/minecraft/world/World;Lnet/minecraft/client/render/Camera;Lnet/minecraft/util/hit/HitResult;)V"
        )
    )
    private void configureAttachableRenderDispatcher(
        RenderTickCounter tickCounter,
        boolean renderBlockOutline,
        Camera camera,
        GameRenderer gameRenderer,
        LightmapTextureManager lightmapTextureManager,
        Matrix4f positionMatrix,
        Matrix4f projectionMatrix,
        CallbackInfo ci
    ) {
        ((MinecraftClientDuck) this.client).chowl$getAttachableRenderDispatcher().configure(
            this.client.world,
            camera,
            this.client.crosshairTarget
        );

    }

    @Inject(method = "setWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;setWorld(Lnet/minecraft/world/World;)V"))
    private void setAttachableRenderDispatcherWorld(
        ClientWorld world, CallbackInfo ci
    ) {
        ((MinecraftClientDuck) this.client).chowl$getAttachableRenderDispatcher().setWorld(world);
    }

    @WrapWithCondition(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"
        )
    )
    private boolean renderTargetAttachableOutline(
        WorldRenderer instance,
        MatrixStack matrices,
        VertexConsumer vertexConsumer,
        Entity entity,
        double cameraX,
        double cameraY,
        double cameraZ,
        BlockPos pos,
        BlockState state,
        @Local(argsOnly = true) Camera camera,
        @Local VertexConsumerProvider.Immediate vertexConsumers,
        @Local BlockHitResult hitResult
    ) {
        var targetAttachable = ((HitResultDuck) hitResult).chowl$getHitAttachable();
        if (targetAttachable == null) return true;

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

        return false;
    }
}
