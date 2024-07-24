package com.chyzman.chowl.logistics.registry;

import com.chyzman.chowl.logistics.item.ImportPanel;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.Item;

public class LogisticsItems implements ItemRegistryContainer {
    public static final ImportPanel IMPORT_PANEL = new ImportPanel(new Item.Settings().maxCount(1));
}
