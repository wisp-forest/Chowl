package com.chyzman.chowl.visage;

import com.chyzman.chowl.visage.registry.VisageBlocks;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ChowlVisage implements ModInitializer {
    public static final String MODID = "chowl-visage";

    //TODO make this addon to industries if it can
    public static final OwoItemGroup VISAGE_GROUP = OwoItemGroup.builder(id("group"), () -> Icon.of(VisageBlocks.VISAGE_BLOCK.asItem()))
            .initializer(group -> {
                group.addCustomTab(Icon.of(VisageBlocks.VISAGE_BLOCK.asItem()), "general", (context, entries) -> {
                    entries.add(new ItemStack(VisageBlocks.VISAGE_BLOCK.asItem()));
                    entries.add(new ItemStack(VisageBlocks.VISAGE_STAIRS.asItem()));
                    entries.add(new ItemStack(VisageBlocks.VISAGE_SLAB.asItem()));
                }, true);
            })
            .build();

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(VisageBlocks.class, MODID, true);

        VISAGE_GROUP.initialize();
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
