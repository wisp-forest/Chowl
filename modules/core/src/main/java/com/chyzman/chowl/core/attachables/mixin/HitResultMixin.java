package com.chyzman.chowl.core.attachables.mixin;

import com.chyzman.chowl.core.attachables.impl.AttachableContainer;
import com.chyzman.chowl.core.attachables.pond.HitResultDuck;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

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
