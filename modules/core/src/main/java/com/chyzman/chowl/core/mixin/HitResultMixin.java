package com.chyzman.chowl.core.mixin;

import com.chyzman.chowl.core.attachable.Attachable;
import com.chyzman.chowl.core.attachable.AttachableContainer;
import com.chyzman.chowl.core.attachable.client.AttachableRenderDispatcher;
import com.chyzman.chowl.core.pond.HitResultDuck;
import com.chyzman.chowl.core.pond.MinecraftClientDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HitResult.class)
public class HitResultMixin implements HitResultDuck {
    @Unique public AttachableContainer hitAttachable;

    @Override
    public void chowl$setHitAttachable(@NotNull AttachableContainer attachable) {
        this.hitAttachable = attachable;
    }

    @Override
    public @Nullable AttachableContainer chowl$getHitAttachable() {
        return this.hitAttachable;
    }
}
