package com.chyzman.chowl.core.multipart.mixin;

import com.chyzman.chowl.core.multipart.pond.MultipartHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HitResult.class)
public class HitResultMixin implements MultipartHitResult {
    @Unique public short hitMultipart = -1;

    @Override
    public void chowl$setHitMultipart(short part) {
        this.hitMultipart = part;
    }

    @Override
    public short chowl$getHitMultipart() {
        return this.hitMultipart;
    }
}
