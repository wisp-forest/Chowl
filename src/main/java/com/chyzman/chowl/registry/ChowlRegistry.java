package com.chyzman.chowl.registry;

import com.chyzman.chowl.block.DrawerFrameBlock;
import com.chyzman.chowl.item.DrawerPanelItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

public class ChowlRegistry {
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, id(name), item);
    }

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, id(name), block);
    }

    public static final Item DRAWER_PANEL_ITEM = registerItem("drawer_panel", new DrawerPanelItem(new Item.Settings().maxCount(1)));

    public static final Block DRAWER_FRAME_BLOCK = registerBlock("drawer_frame",
            new DrawerFrameBlock(
                    AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
                            .nonOpaque()
                            .allowsSpawning(Blocks::never)
                            .solidBlock(Blocks::never)
                            .suffocates(Blocks::never)
                            .blockVision(Blocks::never)
            )
    );
    public static final Item DRAWER_FRAME_ITEM = registerItem("drawer_frame", new BlockItem(DRAWER_FRAME_BLOCK, new Item.Settings()));

    public static void init() {
    }
}