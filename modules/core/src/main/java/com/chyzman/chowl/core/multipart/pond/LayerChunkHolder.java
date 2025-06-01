package com.chyzman.chowl.core.multipart.pond;

import com.chyzman.chowl.core.multipart.layer.MultipartChunk;
import org.jetbrains.annotations.Nullable;

public interface LayerChunkHolder {
    @Nullable MultipartChunk chowl$getMultipartLayer();
}
