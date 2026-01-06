package com.chyzman.chowl.test.registry;

import com.chyzman.chowl.test.ChowlTest;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TestBlockEntities {
    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder<T> builder) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ChowlTest.id(id), builder.build());
    }

    public static void init() {}
}
