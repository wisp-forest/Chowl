package com.chyzman.chowl.visage.registry;

import com.chyzman.chowl.industries.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.visage.block.VisageRenameMeLaterBlock;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.chyzman.chowl.visage.block.VisageRenameMeLaterBlock.STATE_TO_LUMINANCE;

public class VisageBlocks implements BlockRegistryContainer {
    public static final Block RENAME_ME_LATER = new VisageRenameMeLaterBlock(
        AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
            .nonOpaque()
            .dynamicBounds()
            .allowsSpawning(Blocks::never)
            .solidBlock(Blocks::never)
            .suffocates(Blocks::never)
            .blockVision(Blocks::never)
            .luminance(STATE_TO_LUMINANCE)
    );

    public static class Entities implements AutoRegistryContainer<BlockEntityType<?>> {

        public static BlockEntityType<DrawerFrameBlockEntity> RENAME_ME_LATER =
            BlockEntityType.Builder.create(DrawerFrameBlockEntity::new, VisageBlocks.RENAME_ME_LATER)
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
