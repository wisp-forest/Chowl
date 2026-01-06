package com.chyzman.chowl.visage;

import com.chyzman.chowl.core.ChowlItemGroup;
import io.wispforest.owo.itemgroup.Icon;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

import static net.minecraft.world.item.Items.POTATO;

public class Visage implements ModInitializer {
    public static final String MODID = "chowl-visage";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @Override
    public void onInitialize() {
        ChowlItemGroup.proposeIcon(() -> Icon.of(POTATO.asItem()), 80);

        ChowlItemGroup.addInitializer(group -> {
            group.addCustomTab(Icon.of(POTATO.asItem()), "visage", (context, entries) -> {
            }, false);
        }, 80);
    }
}
