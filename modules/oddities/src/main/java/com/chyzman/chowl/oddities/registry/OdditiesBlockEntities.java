package com.chyzman.chowl.oddities.registry;

import com.chyzman.chowl.oddities.Oddities;
import com.chyzman.chowl.oddities.blockentity.ClipboardBlockEntity;
import com.chyzman.chowl.oddities.blockentity.render.ClipboardBlockEntityRenderer;
import com.chyzman.chowl.oddities.infoDisplay.blockEntity.InfoDisplayBlockEntity;
import com.chyzman.chowl.oddities.infoDisplay.blockEntity.renderer.InfoDisplayBlockEntityRenderer;
import com.chyzman.chowl.oddities.infoDisplay.impl.CalendarBlockEntityRenderer;
import com.chyzman.chowl.oddities.infoDisplay.impl.ClockBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.mutable.MutableObject;

public class OdditiesBlockEntities {

    public static final BlockEntityType<ClipboardBlockEntity> CLIPBOARD = register(
            "clipboard",
            ClipboardBlockEntity::new,
            OdditiesBlocks.CLIPBOARD
    );

    public static final BlockEntityType<InfoDisplayBlockEntity> CLOCK = registerInfoDisplay("clock");

    public static final BlockEntityType<InfoDisplayBlockEntity> CALENDAR = registerInfoDisplay("calendar");

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Oddities.id(id), FabricBlockEntityTypeBuilder.create(factory, blocks).build());
    }

    private static BlockEntityType<InfoDisplayBlockEntity> registerInfoDisplay(String id) {
        MutableObject<BlockEntityType<InfoDisplayBlockEntity>> typeRef = new MutableObject<>();
        var type = FabricBlockEntityTypeBuilder.create(
                (pos, state) -> new InfoDisplayBlockEntity(typeRef.getValue(), pos, state)
        ).build();
        typeRef.setValue(type);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Oddities.id(id), type);
    }

    public static void init() {
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        BlockEntityRendererFactories.register(OdditiesBlockEntities.CLIPBOARD, ClipboardBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(OdditiesBlockEntities.CLOCK, ctx -> new ClockBlockEntityRenderer());
        BlockEntityRendererFactories.register(OdditiesBlockEntities.CALENDAR, ctx -> new CalendarBlockEntityRenderer());
    }
}
