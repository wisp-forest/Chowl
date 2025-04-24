package com.chyzman.chowl.core.registry;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.block.FrameBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class CoreBlocks {
    public static final Block DRAWER_FRAME = register(
      "drawer_frame",
      FrameBlock::new,
      AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
        .nonOpaque()
        .dynamicBounds()
        .allowsSpawning(Blocks::never)
        .solidBlock(Blocks::never)
        .suffocates(Blocks::never)
        .blockVision(Blocks::never)
//      .luminance(FrameBlock.STATE_TO_LUMINANCE)
    );

    private static Block register(String id, BlockFactory factory, AbstractBlock.Settings settings) {
        Identifier chowlId = Chowl.id(id);
        Block block = Registry.register(Registries.BLOCK, chowlId, factory.create(settings.registryKey(RegistryKey.of(RegistryKeys.BLOCK, chowlId))));
        Registry.register(Registries.ITEM, chowlId, new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, chowlId))));
        return block;
    }

    private static Block registerWithoutItem(String id, Block block) {
        return Registry.register(Registries.BLOCK, Chowl.id(id), block);
    }

    public static void init() {
    }

    @FunctionalInterface
    interface BlockFactory {
        Block create(Block.Settings settings);
    }
}
