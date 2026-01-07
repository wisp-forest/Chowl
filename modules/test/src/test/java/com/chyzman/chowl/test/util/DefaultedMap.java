package com.chyzman.chowl.test.util;

import java.util.HashMap;
import java.util.function.Supplier;

public class DefaultedMap<K, V> extends HashMap<K, V> {
    private final Supplier<V> defaultSupplier;
    public DefaultedMap(Supplier<V> defaultSupplier) {
        this.defaultSupplier = defaultSupplier;
    }

    public V getOrCreate(K key) {
        return super.computeIfAbsent(key, k -> defaultSupplier.get());
    }
}
