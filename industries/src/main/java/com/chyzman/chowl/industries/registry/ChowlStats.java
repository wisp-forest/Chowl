package com.chyzman.chowl.industries.registry;

import com.chyzman.chowl.industries.Chowl;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;

public class ChowlStats implements AutoRegistryContainer<Identifier> {
    public static final Identifier ITEMS_INSERTED_STAT = Chowl.id("items_inserted");
    public static final Identifier ITEMS_EXTRACTED_STAT = Chowl.id("items_extracted");

    @Override
    public void postProcessField(String namespace, Identifier value, String identifier, Field field) {
        Stats.CUSTOM.getOrCreateStat(value);
    }

    @Override
    public Registry<Identifier> getRegistry() {
        return Registries.CUSTOM_STAT;
    }

    @Override
    public Class<Identifier> getTargetFieldType() {
        return Identifier.class;
    }
}
