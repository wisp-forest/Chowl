package com.chyzman.chowl.util;

public interface InfallibleCloseable extends AutoCloseable {
    @Override
    void close();
}
