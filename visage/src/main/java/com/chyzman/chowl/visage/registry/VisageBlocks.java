package com.chyzman.chowl.visage.registry;

import com.chyzman.chowl.visage.block.*;
import com.chyzman.chowl.visage.block.impl.VisageBlock;
import com.chyzman.chowl.visage.block.impl.VisageFenceBlock;
import com.chyzman.chowl.visage.block.impl.VisageSlabBlock;
import com.chyzman.chowl.visage.block.impl.VisageStairsBlock;
import com.chyzman.chowl.visage.item.VisageBlockItem;
import io.wispforest.owo.registration.reflect.BlockEntityRegistryContainer;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import static com.chyzman.chowl.visage.block.VisageBlockTemplate.STATE_TO_LUMINANCE;

public class VisageBlocks implements BlockRegistryContainer {
    private static final AbstractBlock.Settings SETTINGS = AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
        .nonOpaque()
        .dynamicBounds()
        .allowsSpawning(Blocks::never)
        .solidBlock(Blocks::never)
        .suffocates(Blocks::never)
        .blockVision(Blocks::never)
        .luminance(STATE_TO_LUMINANCE);

    public static final Block VISAGE_BLOCK = new VisageBlock(SETTINGS);
    public static final Block VISAGE_STAIRS = new VisageStairsBlock(VISAGE_BLOCK.getDefaultState(), SETTINGS);
    public static final Block VISAGE_SLAB = new VisageSlabBlock(SETTINGS);
    public static final Block VISAGE_FENCE = new VisageFenceBlock(SETTINGS);

    @Override
    public BlockItem createBlockItem(Block block, String identifier) {
        return new VisageBlockItem(block, new Item.Settings());
    }

    public static class Entities implements BlockEntityRegistryContainer {
        public static BlockEntityType<VisageBlockEntity> VISAGE_BLOCK =
            BlockEntityType.Builder.create(
                VisageBlockEntity::new, VisageBlocks.VISAGE_BLOCK, VisageBlocks.VISAGE_STAIRS, VisageBlocks.VISAGE_SLAB, VisageBlocks.VISAGE_FENCE
            )
                .build();
    }
}
