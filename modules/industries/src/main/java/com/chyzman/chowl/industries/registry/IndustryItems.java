package com.chyzman.chowl.industries.registry;

import com.chyzman.chowl.industries.Industries;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

public class IndustryItems {

    private static Item register(String id, Function<Item.Settings, Item> factory) {
        return Items.register(RegistryKey.of(RegistryKeys.ITEM, Industries.id(id)), factory);
    }

    public static void init() {
    }
}
