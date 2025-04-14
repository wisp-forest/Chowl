package com.chyzman.chowl.oddities.registry;

import com.chyzman.chowl.core.megaBlock.api.MegaBlock;
import com.chyzman.chowl.core.megaBlock.api.MegaBlockItem;
import com.chyzman.chowl.oddities.Oddities;
import com.chyzman.chowl.oddities.item.PinItem;
import com.chyzman.chowl.oddities.item.StickyNoteItem;
import com.chyzman.chowl.oddities.item.StringItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

import java.util.function.Function;

public class OdditiesItems {
    public static final Item CAUTION_BLOCK = register(
            "caution_block",
            new Item.Settings(),
            settings -> new BlockItem(OdditiesBlocks.CAUTION_BLOCK, settings)
    );

    public static final Item CLIPBOARD = register(
            "clipboard",
            new Item.Settings()
                    .maxCount(1)
                    .component(OdditiesComponents.CLIPBOARD_CONTENT, Text.empty()),
            settings -> new BlockItem(OdditiesBlocks.CLIPBOARD, settings)
    );

    public static final Item WATCHER = register(
            "watcher",
            new Item.Settings(),
            settings -> new BlockItem(OdditiesBlocks.WATCHER, settings)
    );

    public static final Item KNOCKER = register(
            "knocker",
            new Item.Settings(),
            settings -> new BlockItem(OdditiesBlocks.KNOCKER, settings)
    );

    public static final Item STICKY_NOTE = register(
            "sticky_note",
            new Item.Settings(),
            StickyNoteItem::new
    );

    public static final Item PIN = register(
            "pin",
            new Item.Settings(),
            PinItem::new
    );

    public static final Item STRING = register(
            "string",
            new Item.Settings(),
            StringItem::new
    );

    public static final Item CLOCK = register(
            "clock",
            new Item.Settings(),
            settings -> new VerticallyAttachableBlockItem(
                    OdditiesBlocks.CLOCK,
                    OdditiesBlocks.WALL_CLOCK,
                    Direction.DOWN,
                    settings
            )
    );

    public static final Item CALENDAR = register(
            "calendar",
            new Item.Settings(),
            settings -> new VerticallyAttachableBlockItem(
                    OdditiesBlocks.CALENDAR,
                    OdditiesBlocks.WALL_CALENDAR,
                    Direction.DOWN,
                    settings
            )
    );

    public static final Item BIG_DIRT = register(
            "big_dirt",
            new Item.Settings(),
            settings -> new MegaBlockItem((MegaBlock) OdditiesBlocks.BIG_DIRT, settings)
    );

    private static Item register(String id, Item.Settings settings, Function<Item.Settings, Item> factory) {
        return Items.register(RegistryKey.of(RegistryKeys.ITEM, Oddities.id(id)), factory, settings);
    }

    public static void init() {
    }
}
