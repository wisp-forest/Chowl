package com.chyzman.chowl.electromechanics.registry;

import com.chyzman.chowl.electromechanics.Electromechanics;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ElectromechanicsBlockEntities {
    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Electromechanics.id(id), FabricBlockEntityTypeBuilder.create(factory, blocks).build());
    }

    public static void init() {
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
    }
}
