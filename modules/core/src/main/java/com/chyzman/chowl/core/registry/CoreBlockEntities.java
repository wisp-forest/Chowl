package com.chyzman.chowl.core.registry;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.block.FrameBlock;
import com.chyzman.chowl.core.blockentity.FrameBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class CoreBlockEntities {
    public static final BlockEntityType<FrameBlockEntity> DRAWER_FRAME =
      register(
        "drawer_frame",
        FabricBlockEntityTypeBuilder.create(
          FrameBlockEntity::new,
          CoreBlocks.DRAWER_FRAME)
      );

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder<T> builder) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Chowl.id(id), builder.build());
    }

    public static void init() {}
}
