package com.chyzman.chowl.logistics;

import com.chyzman.chowl.logistics.registry.LogisticsItems;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ChowlLogistics implements ModInitializer {
    public static final String MODID = "chowl-logistics";

    //TODO make this addon to industries if it can
    public static final OwoItemGroup LOGISTICS_GROUP = OwoItemGroup.builder(id("group"), () -> Icon.of(LogisticsItems.INPUT_ACCESS_PANEL))
        .initializer(group -> {
            group.addCustomTab(Icon.of(LogisticsItems.INPUT_ACCESS_PANEL), "general", (context, entries) -> {
                entries.add(new ItemStack(LogisticsItems.INPUT_ACCESS_PANEL.asItem()));
            }, true);
        })
        .build();

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(LogisticsItems.class, MODID, true);

        LOGISTICS_GROUP.initialize();
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
