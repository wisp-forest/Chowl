package com.chyzman.chowl.core.attachables.pond;

import com.chyzman.chowl.core.attachables.api.client.AttachableRenderDispatcher;

public interface MinecraftClientDuck {
    default AttachableRenderDispatcher chowl$getAttachableRenderDispatcher() {
        throw new UnsupportedOperationException("You shouldn't see this");
    }
}
