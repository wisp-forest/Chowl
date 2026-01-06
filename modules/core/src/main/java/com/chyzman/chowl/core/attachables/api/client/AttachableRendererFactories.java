package com.chyzman.chowl.core.attachables.api.client;

import com.chyzman.chowl.core.attachables.api.Attachable;
import com.chyzman.chowl.core.attachables.api.AttachableType;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class AttachableRendererFactories {
    private static final Map<AttachableType<?>, AttachableRendererFactory<?>> FACTORIES = new HashMap<>();

    public static <T extends Attachable> void register(AttachableType<? extends T> type, AttachableRendererFactory<T> factory) {
        FACTORIES.put(type, factory);
    }

    public static Map<AttachableType<?>, AttachableRenderer<?>> reload(AttachableRendererFactory.Context args) {
        ImmutableMap.Builder<AttachableType<?>, AttachableRenderer<?>> builder = ImmutableMap.builder();
        FACTORIES.forEach((type, factory) -> {
            try {
                builder.put(type, factory.create(args));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to create model for " + ChowlRegistries.ATTACHABLE_TYPE.getKey(type), e);
            }
        });
        return builder.build();
    }
}
