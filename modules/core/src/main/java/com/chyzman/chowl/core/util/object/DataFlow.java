package com.chyzman.chowl.core.util.object;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DataFlow {
    @Nullable
    public static <T, R> R nullable(@Nullable T value, Function<T, @Nullable R> ifNotNull) {
        if (value == null) return null;
        return ifNotNull.apply(value);
    }

    @NotNull
    public static <T, R> R tryOrDefault(@Nullable T value, Function<T, @NotNull R> ifNotNull, @NotNull R defaultValue) {
        if (value == null) return defaultValue;
        return ifNotNull.apply(value);
    }
}
