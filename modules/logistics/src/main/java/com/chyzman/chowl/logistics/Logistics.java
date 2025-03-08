package com.chyzman.chowl.logistics;

import com.chyzman.chowl.core.ChowlItemGroup;
import io.wispforest.owo.itemgroup.Icon;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import static net.minecraft.item.Items.IRON_BLOCK;

public class Logistics implements ModInitializer {
    public static final String MODID = "chowl-logistics";

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }

    @Override
    public void onInitialize() {
        ChowlItemGroup.proposeIcon(() -> Icon.of(IRON_BLOCK.asItem()), 90);

        ChowlItemGroup.addInitializer(group -> {
            group.addCustomTab(Icon.of(IRON_BLOCK.asItem()), "logistics", (context, entries) -> {
            }, false);
        }, 90);
    }
}
