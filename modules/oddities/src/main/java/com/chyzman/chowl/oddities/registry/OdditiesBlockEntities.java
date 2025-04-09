package com.chyzman.chowl.oddities.registry;

import com.chyzman.chowl.oddities.Oddities;
import com.chyzman.chowl.oddities.blockentity.ClipboardBlockEntity;
import com.chyzman.chowl.oddities.blockentity.ClockBlockEntity;
import com.chyzman.chowl.oddities.blockentity.render.ClipboardBlockEntityRenderer;
import com.chyzman.chowl.oddities.blockentity.render.ClockBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class OdditiesBlockEntities {

    public static final BlockEntityType<ClipboardBlockEntity> CLIPBOARD = register(
            "clipboard",
            ClipboardBlockEntity::new,
            OdditiesBlocks.CLIPBOARD
    );

    public static final BlockEntityType<ClockBlockEntity> CLOCK = register(
            "clock",
            ClockBlockEntity::new,
            OdditiesBlocks.CLOCK
    );

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Oddities.id(id), FabricBlockEntityTypeBuilder.create(factory, blocks).build());
    }

    public static void init() {
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        BlockEntityRendererFactories.register(OdditiesBlockEntities.CLIPBOARD, ClipboardBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(OdditiesBlockEntities.CLOCK, ClockBlockEntityRenderer::new);
    }
}
