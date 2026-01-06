package com.chyzman.chowl.core.attachables.api;

import com.chyzman.chowl.core.attachables.impl.AttachableHolder;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import com.chyzman.chowl.core.registry.ChowlRegistryKeys;
import io.wispforest.endec.StructEndec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class AttachableType<T extends Attachable> implements EntityTypeTest<Attachable, T> {
    private final Holder.Reference<AttachableType<?>> registryEntry = ChowlRegistries.ATTACHABLE_TYPE.createIntrusiveHolder(this);

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
        var key = ResourceKey.create(ChowlRegistryKeys.ATTACHABLE_TYPE, id);
        return Registry.register(ChowlRegistries.ATTACHABLE_TYPE, key, type.build(key));
    }

    public static <T extends Attachable> AttachableType<T> register(ResourceKey<AttachableType<?>> key, AttachableType.Builder<T> type) {
        return Registry.register(ChowlRegistries.ATTACHABLE_TYPE, key, type.build(key));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Nullable
    public T create(Level world, Consumer<T> initializer) {
        var attachable = this.factory.create(this);

        initializer.accept(attachable);

        var attachableHolder = world.getAttachedOrCreate(AttachableHolder.TYPE);

        attachableHolder.addAttachable(attachable);

        world.setAttached(AttachableHolder.TYPE, attachableHolder);

        return attachable;
    }

    public boolean isIn(TagKey<AttachableType<?>> tag) {
        return this.registryEntry.is(tag);
    }

    public boolean isIn(HolderSet<AttachableType<?>> entityTypeEntryList) {
        return entityTypeEntryList.contains(this.registryEntry);
    }

    @Override
    public T tryCast(Attachable attachable) {
        return (T) (attachable.getType() == this ? attachable : null);
    }

    @Override
    public Class<? extends Attachable> getBaseClass() {
        return Attachable.class;
    }


    public static class Builder<T extends Attachable> {
        private final AttachableType.AttachableFactory<T> factory;
        private final StructEndec<T> endec;
        private Function<ResourceKey<AttachableType<?>>, String> translationKey = registryKey -> Util.makeDescriptionId("attachable", registryKey.identifier());

        public Builder(AttachableType.AttachableFactory<T> factory, StructEndec<T> endec) {
            this.factory = factory;
            this.endec = endec;
        }

        public AttachableType<T> build(ResourceKey<AttachableType<?>> registryKey) {
            return new AttachableType<>(
                    factory,
                    endec,
                    this.translationKey.apply(registryKey)
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
