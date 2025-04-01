package com.chyzman.chowl.core.mixin.client;

import com.chyzman.chowl.core.pond.MinecraftClientDuck;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.world.ClientWorld;
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

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;configure(Lnet/minecraft/world/World;Lnet/minecraft/client/render/Camera;Lnet/minecraft/util/hit/HitResult;)V"))
    private void configureAttachableRenderDispatcher(
            ObjectAllocator allocator,
            RenderTickCounter tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            Matrix4f positionMatrix,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        ((MinecraftClientDuck)this.client).chowl$getAttachableRenderDispatcher().configure(
                this.client.world,
                camera,
                this.client.crosshairTarget
        );

    }

    @Inject(method = "setWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;setWorld(Lnet/minecraft/world/World;)V"))
    private void setAttachableRenderDispatcherWorld(
            ClientWorld world, CallbackInfo ci
    ) {
        ((MinecraftClientDuck)this.client).chowl$getAttachableRenderDispatcher().setWorld(world);
    }
}
