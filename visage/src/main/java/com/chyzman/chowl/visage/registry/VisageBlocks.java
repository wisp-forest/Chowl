package com.chyzman.chowl.visage.registry;

import com.chyzman.chowl.visage.block.VisageRenameMeLaterBlock;
import com.chyzman.chowl.visage.block.VisageRenameMeLaterBlockEntity;
import com.chyzman.chowl.visage.block.VisageStairsBlock;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.chyzman.chowl.visage.block.VisageBlockTemplate.STATE_TO_LUMINANCE;

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

    public static final Block VISAGE_STAIRS = new VisageStairsBlock(
        RENAME_ME_LATER.getDefaultState(),
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

        public static BlockEntityType<VisageRenameMeLaterBlockEntity> RENAME_ME_LATER =
            BlockEntityType.Builder.create(
                VisageRenameMeLaterBlockEntity::new, VisageBlocks.RENAME_ME_LATER, VisageBlocks.VISAGE_STAIRS
            )
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
