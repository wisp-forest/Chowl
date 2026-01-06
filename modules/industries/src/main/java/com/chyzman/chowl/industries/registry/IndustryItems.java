package com.chyzman.chowl.industries.registry;

import com.chyzman.chowl.industries.Industries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Function;

public class IndustryItems {

    private static Item register(String id, Function<Item.Properties, Item> factory) {
        return Items.registerItem(ResourceKey.create(Registries.ITEM, Industries.id(id)), factory);
    }

    public static void init() {
    }
}
