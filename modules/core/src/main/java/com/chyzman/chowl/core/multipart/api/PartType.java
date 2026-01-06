package com.chyzman.chowl.core.multipart.api;

import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import io.wispforest.endec.StructEndec;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class PartType<T extends Part> {
    private final StructEndec<T> endec;
    private final PartFactory<T> factory;
    private final Holder.Reference<PartType<?>> registryEntry = ChowlRegistries.PART.createIntrusiveHolder(this);

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

    public Holder.Reference<PartType<?>> getRegistryEntry() {
        return registryEntry;
    }

    public Identifier getId() {
        return registryEntry.unwrapKey().map(ResourceKey::identifier).orElseThrow(() -> new IllegalStateException("Can not get ID of unregistered part"));
    }

    public interface PartFactory<T extends Part> {
        T create(List<Part> parts);
    }
}
