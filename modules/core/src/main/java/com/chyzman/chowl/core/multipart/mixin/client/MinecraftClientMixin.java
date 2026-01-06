package com.chyzman.chowl.core.multipart.mixin.client;

import com.chyzman.chowl.core.multipart.api.client.render.PartRenderDispatcher;
import com.chyzman.chowl.core.multipart.pond.MinecraftClientDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin implements MinecraftClientDuck {
    @Unique public PartRenderDispatcher partRenderDispatcher;

    @Shadow @Final public Font font;
    @Shadow @Final private ItemRenderer itemRenderer;
    @Shadow @Final private ItemModelResolver itemModelResolver;
    @Shadow @Final private BlockRenderDispatcher blockRenderer;
    @Shadow @Final private BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;
    @Shadow @Final private ReloadableResourceManager resourceManager;
    @Shadow @Final private ModelManager modelManager;
    @Shadow @Final private AtlasManager atlasManager;
    @Shadow @Final private PlayerSkinRenderCache playerSkinRenderCache;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;<init>(Lnet/minecraft/client/gui/Font;Ljava/util/function/Supplier;Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;Lnet/minecraft/client/renderer/item/ItemModelResolver;Lnet/minecraft/client/renderer/entity/ItemRenderer;Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;Lnet/minecraft/client/resources/model/MaterialSet;Lnet/minecraft/client/renderer/PlayerSkinRenderCache;)V"))
    private void createAttachableRenderDispatcher(
            GameConfig args,
            CallbackInfo ci
    ) {
        this.partRenderDispatcher = new PartRenderDispatcher(
          this.font,
          this.modelManager.entityModels(),
          this.blockRenderer,
          this.blockEntityRenderDispatcher,
          this.itemModelResolver,
          this.itemRenderer,
          this.entityRenderDispatcher,
          this.atlasManager,
          this.playerSkinRenderCache
        );
        resourceManager.registerReloadListener(this.partRenderDispatcher);
    }

    @Override
    public PartRenderDispatcher chowl$getPartRenderDispatcher() {
        return this.partRenderDispatcher;
    }
}
