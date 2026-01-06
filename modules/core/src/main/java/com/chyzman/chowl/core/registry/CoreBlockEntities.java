package com.chyzman.chowl.core.registry;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.blockentity.FrameBlockEntity;
import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CoreBlockEntities {
    public static final BlockEntityType<MultipartBlockEntity> MULTIPART =
      register(
        "multipart",
        FabricBlockEntityTypeBuilder.create(MultipartBlockEntity::new)
      );

    public static final BlockEntityType<FrameBlockEntity> FRAME =
        register(
            "frame",
            FabricBlockEntityTypeBuilder.create(FrameBlockEntity::new)
              .addBlock(CoreBlocks.DRAWER_FRAME)
        );

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder<T> builder) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Chowl.id(id), builder.build());
    }

    public static void init() {

    }
}
