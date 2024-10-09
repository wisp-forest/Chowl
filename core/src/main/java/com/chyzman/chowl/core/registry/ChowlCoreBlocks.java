package com.chyzman.chowl.core.registry;

import com.chyzman.chowl.core.block.FrameBlock;
import com.chyzman.chowl.core.blockentity.FrameBlockEntity;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ChowlCoreBlocks implements BlockRegistryContainer {
    public static final Block FRAME = new FrameBlock(
        AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
            .nonOpaque()
            .dynamicBounds()
            .allowsSpawning(Blocks::never)
            .solidBlock(Blocks::never)
            .suffocates(Blocks::never)
            .blockVision(Blocks::never)
//            .luminance(DrawerFrameBlock.STATE_TO_LUMINANCE)
    );

    public static class Entities implements AutoRegistryContainer<BlockEntityType<?>> {

        public static BlockEntityType<FrameBlockEntity> FRAME =
            BlockEntityType.Builder.create(FrameBlockEntity::new, ChowlCoreBlocks.FRAME)
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
