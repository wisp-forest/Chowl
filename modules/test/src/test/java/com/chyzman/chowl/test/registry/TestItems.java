package com.chyzman.chowl.test.registry;

import com.chyzman.chowl.test.ChowlTest;
import com.chyzman.chowl.test.item.FramePanelItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class TestItems {
    public static final Item FRAME_PANEL = register(
      "frame_panel",
      FramePanelItem::new,
      new Item.Properties()
    );

    private static Item register(String id, ItemFactory factory, Item.Properties properties) {
        Identifier chowlId = ChowlTest.id(id);
        return Registry.register(BuiltInRegistries.ITEM, chowlId, factory.create(properties.setId(ResourceKey.create(Registries.ITEM, chowlId))));
    }

    public static void init() {
    }

    @FunctionalInterface
    interface ItemFactory {
        Item create(Item.Properties settings);
    }
}
