package com.chyzman.chowl.core.pond;

import org.jetbrains.annotations.Nullable;

public interface MultipartHitResult {
    default void chowl$setHitMultipart(short part) {
        throw new UnsupportedOperationException("You shouldn't see this");
    }

    @Nullable
    default short chowl$getHitMultipart() {
        throw new UnsupportedOperationException("You shouldn't see this");
    }
}
