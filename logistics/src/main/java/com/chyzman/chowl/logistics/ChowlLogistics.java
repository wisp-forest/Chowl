package com.chyzman.chowl.logistics;

import com.chyzman.chowl.core.ChowlItemGroup;
import com.chyzman.chowl.logistics.registry.LogisticsItems;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ChowlLogistics implements ModInitializer {
    public static final String MODID = "chowl-logistics";

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(LogisticsItems.class, MODID, true);

        ChowlItemGroup.addInitializer(group -> {
            group.addCustomTab(Icon.of(LogisticsItems.INPUT_ACCESS_PANEL), "logistics", (context, entries) -> {
                entries.add(new ItemStack(LogisticsItems.INPUT_ACCESS_PANEL.asItem()));
            }, false);
        }, 200);
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
