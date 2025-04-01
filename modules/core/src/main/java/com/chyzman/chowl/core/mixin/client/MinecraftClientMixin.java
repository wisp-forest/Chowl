package com.chyzman.chowl.core.mixin.client;

import com.chyzman.chowl.core.attachable.client.AttachableRenderDispatcher;
import com.chyzman.chowl.core.client.ChowlClient;
import com.chyzman.chowl.core.pond.MinecraftClientDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements MinecraftClientDuck {
    @Unique public AttachableRenderDispatcher attachableRenderDispatcher;

    @Shadow @Final private ItemRenderer itemRenderer;

    @Shadow @Final private ItemModelManager itemModelManager;

    @Shadow @Final private BlockRenderManager blockRenderManager;

    @Shadow @Final private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Shadow @Final public TextRenderer textRenderer;

    @Shadow @Final private ReloadableResourceManagerImpl resourceManager;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;<init>(Lnet/minecraft/client/font/TextRenderer;Ljava/util/function/Supplier;Lnet/minecraft/client/render/block/BlockRenderManager;Lnet/minecraft/client/item/ItemModelManager;Lnet/minecraft/client/render/item/ItemRenderer;Lnet/minecraft/client/render/entity/EntityRenderDispatcher;)V"))
    private void createAttachableRenderDispatcher(
            RunArgs args,
            CallbackInfo ci
    ) {
        this.attachableRenderDispatcher = new AttachableRenderDispatcher(
                itemRenderer,
                itemModelManager,
                blockRenderManager,
                blockEntityRenderDispatcher,
                entityRenderDispatcher,
                textRenderer
        );
        resourceManager.registerReloader(this.attachableRenderDispatcher);
    }

    @Override
    public AttachableRenderDispatcher chowl$getAttachableRenderDispatcher() {
        return this.attachableRenderDispatcher;
    }
}
