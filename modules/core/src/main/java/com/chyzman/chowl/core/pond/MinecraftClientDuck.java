package com.chyzman.chowl.core.pond;

import com.chyzman.chowl.core.attachable.client.AttachableRenderDispatcher;

public interface MinecraftClientDuck {
    default AttachableRenderDispatcher chowl$getAttachableRenderDispatcher() {
        throw new UnsupportedOperationException("You shouldn't see this");
    }
}
