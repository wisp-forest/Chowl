package com.chyzman.chowl.core.mixin;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.pond.MultipartHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HitResult.class)
public class HitResultMixin implements MultipartHitResult {
    @Unique public Part hitMultipart;

    @Override
    public void chowl$setHitMultipart(@NotNull Part part) {
        this.hitMultipart = part;
    }

    @Override
    public @Nullable Part chowl$getHitMultipart() {
        return this.hitMultipart;
    }
}
