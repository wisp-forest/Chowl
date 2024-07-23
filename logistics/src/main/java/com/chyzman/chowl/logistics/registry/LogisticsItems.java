package com.chyzman.chowl.logistics.registry;

import com.chyzman.chowl.industries.item.*;
import com.chyzman.chowl.industries.item.component.DisplayingPanelConfig;
import com.chyzman.chowl.industries.registry.ChowlComponents;
import com.chyzman.chowl.logistics.item.InputAccessPanel;
import io.wispforest.lavender.book.LavenderBookItem;
import io.wispforest.owo.registration.annotations.IterationIgnored;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.Item;

import static com.chyzman.chowl.industries.Chowl.id;

public class LogisticsItems implements ItemRegistryContainer {
    public static final Item INPUT_ACCESS_PANEL = new InputAccessPanel(new Item.Settings().maxCount(1));
}
