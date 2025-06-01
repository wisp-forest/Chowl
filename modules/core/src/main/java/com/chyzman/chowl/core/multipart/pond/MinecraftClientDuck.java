package com.chyzman.chowl.core.multipart.pond;

import com.chyzman.chowl.core.multipart.api.client.PartRenderDispatcher;

public interface MinecraftClientDuck {
    default PartRenderDispatcher chowl$getPartRenderDispatcher() {
        throw new UnsupportedOperationException("You shouldn't see this");
    }
}