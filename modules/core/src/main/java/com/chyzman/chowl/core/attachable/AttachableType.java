package com.chyzman.chowl.core.attachable;

import com.chyzman.chowl.core.registry.ChowlRegistries;
import io.wispforest.endec.StructEndec;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class AttachableType<T extends Attachable> implements TypeFilter<Attachable, T> {
    private final RegistryEntry.Reference<AttachableType<?>> registryEntry = ChowlRegistries.ATTACHABLE_TYPE.createEntry(this);

    private final AttachableType.AttachableFactory<T> factory;
    public final StructEndec<T> endec;
    public final String translationKey;

    public AttachableType(
            AttachableType.AttachableFactory<T> factory,
            StructEndec<T> endec,
            String translationKey
    ) {
        this.factory = factory;
        this.endec = endec;
        this.translationKey = translationKey;
    }

    public static <T extends Attachable> AttachableType<T> register(Identifier id, AttachableType.Builder<T> type) {
        var key = RegistryKey.of(ChowlRegistries.ATTACHABLE_TYPE_KEY, id);
        return Registry.register(ChowlRegistries.ATTACHABLE_TYPE, key, type.build(key));
    }

    public static <T extends Attachable> AttachableType<T> register(RegistryKey<AttachableType<?>> key, AttachableType.Builder<T> type) {
        return Registry.register(ChowlRegistries.ATTACHABLE_TYPE, key, type.build(key));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Nullable
    public T create(World world, Consumer<T> initializer) {
        var attachable = this.factory.create(this);

        initializer.accept(attachable);

        var attachableHolder = world.getAttachedOrCreate(AttachableHolder.TYPE);

        attachableHolder.addAttachable(attachable);

        world.setAttached(AttachableHolder.TYPE, attachableHolder);

        return attachable;
    }

    public boolean isIn(TagKey<AttachableType<?>> tag) {
        return this.registryEntry.isIn(tag);
    }

    public boolean isIn(RegistryEntryList<AttachableType<?>> entityTypeEntryList) {
        return entityTypeEntryList.contains(this.registryEntry);
    }

    @Override
    public T downcast(Attachable attachable) {
        return (T) (attachable.getType() == this ? attachable : null);
    }

    @Override
    public Class<? extends Attachable> getBaseClass() {
        return Attachable.class;
    }


    public static class Builder<T extends Attachable> {
        private final AttachableType.AttachableFactory<T> factory;
        private final StructEndec<T> endec;
        private RegistryKeyedValue<AttachableType<?>, String> translationKey = registryKey -> Util.createTranslationKey("attachable", registryKey.getValue());

        public Builder(AttachableType.AttachableFactory<T> factory, StructEndec<T> endec) {
            this.factory = factory;
            this.endec = endec;
        }

        public AttachableType<T> build(RegistryKey<AttachableType<?>> registryKey) {
            return new AttachableType<>(
                    factory,
                    endec,
                    this.translationKey.get(registryKey)
            );
        }
    }

    @FunctionalInterface
    public interface AttachableFactory<T extends Attachable> {
        T create(AttachableType<T> type);
    }

    @Override
    public int hashCode() {
        return this.registryEntry.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AttachableType<?> that)) return false;
        return Objects.equals(this.registryEntry, that.registryEntry);
    }
}
