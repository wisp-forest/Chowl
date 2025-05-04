package com.chyzman.chowl.core.multipart.api;

public interface Multipart<T extends Part> {
    PartType<T> getPart();
}
