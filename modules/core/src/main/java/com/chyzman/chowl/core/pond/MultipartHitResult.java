package com.chyzman.chowl.core.pond;

import com.chyzman.chowl.core.multipart.api.Part;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MultipartHitResult {
    default void chowl$setHitMultipart(@NotNull Part part) {
        throw new UnsupportedOperationException("You shouldn't see this");
    }

    @Nullable
    default Part chowl$getHitMultipart() {
        throw new UnsupportedOperationException("You shouldn't see this");
    }
}
