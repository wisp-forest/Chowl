package com.chyzman.chowl.visage;

import com.chyzman.chowl.core.ChowlItemGroup;
import com.chyzman.chowl.visage.network.ChowlVisageNetworking;
import com.chyzman.chowl.visage.registry.VisageBlocks;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class ChowlVisage implements ModInitializer {
    public static final String MODID = "chowl-visage";

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(VisageBlocks.class, MODID, true);

        ChowlItemGroup.proposeIcon(() -> Icon.of(VisageBlocks.VISAGE_BLOCK.asItem()), 90);

        ChowlItemGroup.addInitializer(group -> {
            group.addCustomTab(Icon.of(VisageBlocks.VISAGE_BLOCK.asItem()), "visage", (context, entries) -> {
                entries.addAll(List.of(
                        new ItemStack(VisageBlocks.VISAGE_BLOCK.asItem()),
                        new ItemStack(VisageBlocks.VISAGE_STAIRS.asItem()),
                        new ItemStack(VisageBlocks.VISAGE_SLAB.asItem()),
                        new ItemStack(VisageBlocks.VISAGE_FENCE.asItem()),
                        new ItemStack(VisageBlocks.VISAGE_WALL.asItem()),
                        new ItemStack(VisageBlocks.VISAGE_DOOR.asItem())
                ));
            }, false);
        }, 300);

        ChowlVisageNetworking.init();
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
