package com.chyzman.chowl.core.util;

public interface InfallibleCloseable extends AutoCloseable {
    @Override
    void close();
}
