package com.chyzman.chowl;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.classes.ChowlIndustriesConfig;
import com.chyzman.chowl.commands.DebugCommands;
import com.chyzman.chowl.recipe.ChowlRecipeSerializers;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.registry.ServerBoundPackets;
import com.chyzman.chowl.registry.ServerEventListeners;
import com.chyzman.chowl.screen.PanelConfigSreenHandler;
import com.chyzman.chowl.upgrade.ExplosiveUpgrade;
import com.chyzman.chowl.upgrade.NukeCoreUpgrade;
import io.wispforest.owo.Owo;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static com.chyzman.chowl.registry.ChowlRegistry.*;
import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

public class Chowl implements ModInitializer {
    public static final String MODID = "chowl-industries";
    public static final ChowlIndustriesConfig CHOWL_CONFIG = ChowlIndustriesConfig.createAndLoad();

    public static final TagKey<Item> EXPLOSIVE_UPGRADE_TAG = TagKey.of(RegistryKeys.ITEM, id("explosive_panel_upgrade"));
    public static final TagKey<Item> FIERY_UPGRADE_TAG = TagKey.of(RegistryKeys.ITEM, id("fiery_panel_upgrade"));
    public static final TagKey<Item> HOPPER_UPGRADE_TAG = TagKey.of(RegistryKeys.ITEM, id("hopper_panel_upgrade"));
    public static final TagKey<Item> LAVA_UPGRADE_TAG = TagKey.of(RegistryKeys.ITEM, id("lava_voiding_panel_upgrade"));
    public static final TagKey<Item> NETHERITE_UPGRADE_TAG = TagKey.of(RegistryKeys.ITEM, id("netherite_panel_upgrade"));
    public static final TagKey<Item> VOID_UPGRADE_TAG = TagKey.of(RegistryKeys.ITEM, id("true_voiding_panel_upgrade"));
    public static final TagKey<Item> GLOWING_UPGRADE_TAG = TagKey.of(RegistryKeys.ITEM, id("glowing_panel_upgrade"));
    public static final TagKey<Item> NAMING_UPGRADE_TAG = TagKey.of(RegistryKeys.ITEM, id("naming_panel_upgrade"));
    public static final TagKey<Item> BLAST_PROOF_UPGRADE_TAG = TagKey.of(RegistryKeys.ITEM, id("blast_proof_panel_upgrade"));


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
                    entries.add(new ItemStack(PHANTOM_PANEL_ITEM));
                    entries.add(new ItemStack(COMPRESSING_PANEL_ITEM));
                    entries.add(new ItemStack(CHOWL_HANDBOOK));
                }, true);
            })
            .build();

    @Override
    public void onInitialize() {
        ChowlRegistry.init();
        ServerBoundPackets.init();
        ServerEventListeners.init();
        ChowlRecipeSerializers.init();
        Registry.register(Registries.SCREEN_HANDLER, id("panel_config"), PanelConfigSreenHandler.TYPE);

        ExplosiveUpgrade.init();

        if (FabricLoader.getInstance().isModLoaded("mythicmetals"))
            NukeCoreUpgrade.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            if (Owo.DEBUG) DebugCommands.register(dispatcher, registryAccess);
        });

        CHOWL_GROUP.initialize();
    }
}