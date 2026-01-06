package com.chyzman.chowl.industries;

import com.chyzman.chowl.core.ChowlItemGroup;
import io.wispforest.owo.itemgroup.Icon;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

import static net.minecraft.world.item.Items.OAK_PLANKS;

public class Industries implements ModInitializer {
    public static final String MODID = "chowl-industries";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @Override
    public void onInitialize() {
        ChowlItemGroup.proposeIcon(() -> Icon.of(OAK_PLANKS.asItem()), 100);

        ChowlItemGroup.addInitializer(group -> {
            group.addCustomTab(Icon.of(OAK_PLANKS.asItem()), "industries", (context, entries) -> {
            }, false);
        }, 100);
    }
}
