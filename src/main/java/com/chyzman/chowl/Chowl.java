package com.chyzman.chowl;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.commands.DebugCommands;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.registry.ServerBoundPackets;
import com.chyzman.chowl.registry.ServerEventListeners;
import com.chyzman.chowl.screen.PanelConfigSreenHandler;
import io.wispforest.owo.Owo;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.chyzman.chowl.registry.ChowlRegistry.*;
import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

public class Chowl implements ModInitializer {
    public static final String MODID = "chowl-industries";
//    public static final ChowlIndustriesConfig CONFIG = ChowlIndustriesConfig.createAndLoad();

    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(id(FabricLoader.getInstance()
        .getModContainer("chowl-industries")
        .orElseThrow()
        .getMetadata()
        .getVersion()
        .getFriendlyString()
    ));

    public static BlockEntityType<DrawerFrameBlockEntity> DRAWER_FRAME_BLOCK_ENTITY_TYPE = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("drawer_frame"), FabricBlockEntityTypeBuilder.create(DrawerFrameBlockEntity::new, ChowlRegistry.DRAWER_FRAME_BLOCK).build());

    public static final OwoItemGroup CHOWL_GROUP = OwoItemGroup.builder(id("group"), () -> Icon.of(DRAWER_FRAME_ITEM))
            .initializer(group -> {
                group.addCustomTab(Icon.of(DRAWER_FRAME_ITEM), "general", (context, entries) -> {
                    entries.add(new ItemStack(DRAWER_FRAME_ITEM));
                    entries.add(new ItemStack(DRAWER_PANEL_ITEM));
                    entries.add(new ItemStack(ACCESS_PANEL_ITEM));
                    entries.add(new ItemStack(MIRROR_PANEL_ITEM));
                    entries.add(new ItemStack(BLANK_PANEL_ITEM));
                }, true);
            })
            .build();

    @Override
    public void onInitialize() {
        ChowlRegistry.init();
        ServerBoundPackets.init();
        ServerEventListeners.init();
        Registry.register(Registries.SCREEN_HANDLER, id("panel_config"), PanelConfigSreenHandler.TYPE);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            if (Owo.DEBUG) DebugCommands.register(dispatcher, registryAccess);
        });

        CHOWL_GROUP.initialize();
    }
}