package com.chyzman.chowl.core.multipart.mixin.client;

import com.chyzman.chowl.core.multipart.api.client.PartRenderDispatcher;
import com.chyzman.chowl.core.multipart.pond.MinecraftClientDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements MinecraftClientDuck {
    @Unique public PartRenderDispatcher partRenderDispatcher;

    @Shadow @Final public TextRenderer textRenderer;
    @Shadow @Final private ItemRenderer itemRenderer;
    @Shadow @Final private ItemModelManager itemModelManager;
    @Shadow @Final private BlockRenderManager blockRenderManager;
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;
    @Shadow @Final private ReloadableResourceManagerImpl resourceManager;
    @Shadow @Final private BakedModelManager bakedModelManager;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;<init>(Lnet/minecraft/client/font/TextRenderer;Ljava/util/function/Supplier;Lnet/minecraft/client/render/block/BlockRenderManager;Lnet/minecraft/client/item/ItemModelManager;Lnet/minecraft/client/render/item/ItemRenderer;Lnet/minecraft/client/render/entity/EntityRenderDispatcher;)V"))
    private void createAttachableRenderDispatcher(
            RunArgs args,
            CallbackInfo ci
    ) {
        this.partRenderDispatcher = new PartRenderDispatcher(
          this.textRenderer,
          this.bakedModelManager.getEntityModelsSupplier(),
          this.blockRenderManager,
          this.itemModelManager,
          this.itemRenderer,
          this.entityRenderDispatcher
        );
        resourceManager.registerReloader(this.partRenderDispatcher);
    }

    @Override
    public PartRenderDispatcher chowl$getPartRenderDispatcher() {
        return this.partRenderDispatcher;
    }
}
