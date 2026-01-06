package com.chyzman.chowl.electromechanics.registry;

import com.chyzman.chowl.electromechanics.Electromechanics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Function;

public class ElectromechanicsItems {

    public static final Item WATCHER = register(
            "watcher",
            new Item.Properties(),
            settings -> new BlockItem(ElectromechanicsBlocks.WATCHER, settings)
    );

    public static final Item KNOCKER = register(
            "knocker",
            new Item.Properties(),
            settings -> new BlockItem(ElectromechanicsBlocks.KNOCKER, settings)
    );

    private static Item register(String id, Item.Properties settings, Function<Item.Properties, Item> factory) {
        return Items.registerItem(ResourceKey.create(Registries.ITEM, Electromechanics.id(id)), factory, settings);
    }

    public static void init() {
    }
}
