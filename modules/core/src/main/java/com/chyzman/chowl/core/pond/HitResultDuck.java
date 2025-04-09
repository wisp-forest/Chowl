package com.chyzman.chowl.core.pond;

import com.chyzman.chowl.core.attachable.AttachableContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HitResultDuck {

    default void chowl$setHitAttachable(@NotNull AttachableContainer attachable) {
        throw new UnsupportedOperationException("You shouldn't see this");
    }

    @Nullable
    default AttachableContainer chowl$getHitAttachable() {
        throw new UnsupportedOperationException("You shouldn't see this");
    }
}
