package com.chyzman.chowl.industries.util;

public interface InfallibleCloseable extends AutoCloseable {
    @Override
    void close();
}
