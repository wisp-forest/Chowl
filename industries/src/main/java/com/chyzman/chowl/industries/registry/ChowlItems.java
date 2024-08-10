package com.chyzman.chowl.industries.registry;

import com.chyzman.chowl.industries.item.*;
import com.chyzman.chowl.industries.item.component.DisplayingPanelConfig;
import io.wispforest.lavender.book.LavenderBookItem;
import io.wispforest.owo.registration.annotations.IterationIgnored;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.Item;

import static com.chyzman.chowl.industries.Chowl.id;

public class ChowlItems implements ItemRegistryContainer {
    @IterationIgnored public static final Item CHOWL_HANDBOOK = LavenderBookItem.registerForBook(id("chowl_handbook"), new Item.Settings().maxCount(1));

    public static final Item DRAWER_PANEL = new DrawerPanelItem(new Item.Settings().maxCount(1));

    public static final Item ACCESS_PANEL = new AccessPanelItem(new Item.Settings()
        .maxCount(1)
        .component(ChowlComponents.DISPLAYING_CONFIG,
            DisplayingPanelConfig.DEFAULT.toBuilder()
                .hideCapacity(true)
                .hideCount(true)
                .hideName(true)
                .build()
        ));

    public static final Item MIRROR_PANEL = new MirrorPanelItem(new Item.Settings().maxCount(1));

    public static final Item PHANTOM_PANEL = new BlankPanelItem(new Item.Settings());

    public static final Item COMPRESSING_PANEL = new CompressingPanelItem(new Item.Settings().maxCount(1));

    public static final Item PACKING_PANEL = new PackingPanelItem(new Item.Settings().maxCount(1));
}
