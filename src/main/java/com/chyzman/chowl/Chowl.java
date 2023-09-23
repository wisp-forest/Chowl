package com.chyzman.chowl;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.registry.ServerBoundPackets;
import com.chyzman.chowl.registry.ServerEventListeners;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

public class Chowl implements ModInitializer {
    public static final String MODID = "chowl-industries";

    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(id(FabricLoader.getInstance().getModContainer("chowl-industries").isPresent() ? FabricLoader.getInstance().getModContainer("chowl-industries").get().getMetadata().getVersion().getFriendlyString() : "main"));

    public static BlockEntityType<DrawerFrameBlockEntity> DRAWER_FRAME_BLOCK_ENTITY_TYPE = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("drawer_frame"), FabricBlockEntityTypeBuilder.create(DrawerFrameBlockEntity::new, ChowlRegistry.DRAWER_FRAME).build());

    @Override
    public void onInitialize() {
        ChowlRegistry.init();
        ServerBoundPackets.init();
        ServerEventListeners.init();
    }
}