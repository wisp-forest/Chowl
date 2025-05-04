package com.chyzman.chowl.core.registry;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CoreBlockEntities {
    public static final BlockEntityType<MultipartBlockEntity> MULTIPART =
      register(
        "multipart",
        FabricBlockEntityTypeBuilder.create(MultipartBlockEntity::new)
      );

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder<T> builder) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Chowl.id(id), builder.build());
    }

    public static void init() {}
}
