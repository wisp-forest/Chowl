package com.chyzman.chowl.core.multipart.api;

import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import io.wispforest.endec.StructEndec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class PartType<T extends Part> {
    private final StructEndec<T> endec;
    private final PartFactory<T> factory;
    private final RegistryEntry.Reference<PartType<?>> registryEntry = ChowlRegistries.PART.createEntry(this);

    public PartType(StructEndec<T> endec, PartFactory<T> factory) {
        this.endec = endec;
        this.factory = factory;
    }

    public PartType(StructEndec<T> endec, Supplier<T> factory) {
        this(endec, (parts) -> factory.get());
    }

    public StructEndec<T> getEndec() {
        return endec;
    }

    public T create(MultipartHolderBlockEntity holder) {
        T part = this.factory.create(holder.getParts());
        part.init(holder);
        return part;
    }

    public RegistryEntry.Reference<PartType<?>> getRegistryEntry() {
        return registryEntry;
    }

    public Identifier getId() {
        return registryEntry.getKey().map(RegistryKey::getValue).orElseThrow(() -> new IllegalStateException("Can not get ID of unregistered part"));
    }

    public interface PartFactory<T extends Part> {
        T create(List<Part> parts);
    }
}
