package com.chyzman.chowl.electromechanics.registry;

import com.chyzman.chowl.electromechanics.Electromechanics;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

public class ElectromechanicsItems {

    public static final Item WATCHER = register(
            "watcher",
            new Item.Settings(),
            settings -> new BlockItem(ElectromechanicsBlocks.WATCHER, settings)
    );

    public static final Item KNOCKER = register(
            "knocker",
            new Item.Settings(),
            settings -> new BlockItem(ElectromechanicsBlocks.KNOCKER, settings)
    );

    private static Item register(String id, Item.Settings settings, Function<Item.Settings, Item> factory) {
        return Items.register(RegistryKey.of(RegistryKeys.ITEM, Electromechanics.id(id)), factory, settings);
    }

    public static void init() {
    }
}
