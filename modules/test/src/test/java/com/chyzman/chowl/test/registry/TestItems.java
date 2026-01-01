package com.chyzman.chowl.test.registry;

import com.chyzman.chowl.test.ChowlTest;
import com.chyzman.chowl.test.item.FramePanelItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class TestItems {
    public static final Item FRAME_PANEL = register(
      "frame_panel",
      FramePanelItem::new,
      new Item.Settings()
    );

    private static Item register(String id, ItemFactory factory, Item.Settings settings) {
        Identifier chowlId = ChowlTest.id(id);
        return Registry.register(Registries.ITEM, chowlId, factory.create(settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, chowlId))));
    }

    public static void init() {
    }

    @FunctionalInterface
    interface ItemFactory {
        Item create(Item.Settings settings);
    }
}
