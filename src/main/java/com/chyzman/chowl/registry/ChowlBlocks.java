package com.chyzman.chowl.registry;

import com.chyzman.chowl.block.DrawerFrameBlock;
import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.DyeColor;

public class ChowlBlocks implements BlockRegistryContainer {
    public static final Block DRAWER_FRAME = new DrawerFrameBlock(
        AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
            .nonOpaque()
            .dynamicBounds()
            .allowsSpawning(Blocks::never)
            .solidBlock(Blocks::never)
            .suffocates(Blocks::never)
            .blockVision(Blocks::never)
            .luminance(DrawerFrameBlock.STATE_TO_LUMINANCE)
    );

    public static final Block CAUTION_BLOCK = new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).mapColor(DyeColor.YELLOW));

    public static class Entities implements AutoRegistryContainer<BlockEntityType<?>> {

        public static BlockEntityType<DrawerFrameBlockEntity> DRAWER_FRAME =
            BlockEntityType.Builder.create(DrawerFrameBlockEntity::new, ChowlBlocks.DRAWER_FRAME)
                .build();

        @Override
        public Registry<BlockEntityType<?>> getRegistry() {
            return Registries.BLOCK_ENTITY_TYPE;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<BlockEntityType<?>> getTargetFieldType() {
            return (Class<BlockEntityType<?>>)(Object) BlockEntityType.class;
        }
    }
}
