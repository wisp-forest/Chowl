package com.chyzman.chowl.core.multipart.mixin.client;

import com.chyzman.chowl.core.multipart.pond.PersistentMeshData;
import com.mojang.blaze3d.vertex.MeshData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MeshData.class)
public abstract class MeshDataMixin implements PersistentMeshData {
    @Shadow public abstract void close();

    @Unique
    private boolean chowl$persistent = false;

    @Override
    public void chowl$setPersistent() {
        this.chowl$persistent = true;
    }

    @Override
    public void chowl$close() {
        this.chowl$persistent = false;
        this.close();
    }

    @Inject(at = @At("HEAD"), method = "close", cancellable = true)
    private void persist(CallbackInfo ci) {
        if (chowl$persistent) ci.cancel();
    }
}
