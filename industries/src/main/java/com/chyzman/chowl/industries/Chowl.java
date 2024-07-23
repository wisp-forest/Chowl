package com.chyzman.chowl.industries;

import com.chyzman.chowl.core.ChowlItemGroup;
import com.chyzman.chowl.core.util.ChannelUtil;
import com.chyzman.chowl.industries.classes.ChowlIndustriesConfig;
import com.chyzman.chowl.industries.commands.DebugCommands;
import com.chyzman.chowl.industries.commands.RandomizeCommand;
import com.chyzman.chowl.industries.recipe.ChowlRecipeSerializers;
import com.chyzman.chowl.industries.registry.*;
import com.chyzman.chowl.industries.screen.PanelConfigSreenHandler;
import com.chyzman.chowl.industries.upgrade.ExplosiveUpgrade;
import com.chyzman.chowl.industries.upgrade.LabelingUpgrade;
import com.chyzman.chowl.industries.upgrade.NukeCoreUpgrade;
import io.wispforest.owo.Owo;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

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
    public static final TagKey<Item> LABELING_UPGRADE_TAG = TagKey.of(RegistryKeys.ITEM, id("labeling_panel_upgrade"));
    public static final TagKey<Item> BLAST_PROOF_UPGRADE_TAG = TagKey.of(RegistryKeys.ITEM, id("blast_proof_panel_upgrade"));


    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(ChannelUtil.getChannelId(MODID))
        .addEndecs(ServerBoundPackets::addEndecs);

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(ChowlBlocks.class, MODID, true);
        FieldRegistrationHandler.register(ChowlComponents.class, MODID, true);
        FieldRegistrationHandler.register(ChowlItems.class, MODID, true);
        FieldRegistrationHandler.register(ChowlCriteria.class, MODID, true);
        FieldRegistrationHandler.register(ChowlStats.class, MODID, true);

        ServerBoundPackets.init();
        ServerEventListeners.init();
        ChowlRecipeSerializers.init();
        Registry.register(Registries.SCREEN_HANDLER, id("panel_config"), PanelConfigSreenHandler.TYPE);

        ExplosiveUpgrade.init();
        LabelingUpgrade.init();

        if (FabricLoader.getInstance().isModLoaded("mythicmetals"))
            NukeCoreUpgrade.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            if (Owo.DEBUG) DebugCommands.register(dispatcher, registryAccess);

            RandomizeCommand.register(dispatcher);
        });

        ChowlItemGroup.proposeIcon(() -> Icon.of(ChowlBlocks.DRAWER_FRAME.asItem()), 100);

        ChowlItemGroup.addInitializer(group -> {
            group.addCustomTab(Icon.of(ChowlBlocks.DRAWER_FRAME.asItem()), "industries", (context, entries) -> {
                entries.add(new ItemStack(ChowlBlocks.DRAWER_FRAME.asItem()));
                entries.add(new ItemStack(ChowlItems.DRAWER_PANEL));
                entries.add(new ItemStack(ChowlItems.ACCESS_PANEL));
                entries.add(new ItemStack(ChowlItems.MIRROR_PANEL));
                entries.add(new ItemStack(ChowlItems.PHANTOM_PANEL));
                entries.add(new ItemStack(ChowlItems.COMPRESSING_PANEL));
                entries.add(new ItemStack(ChowlItems.CHOWL_HANDBOOK));
                entries.add(new ItemStack(ChowlBlocks.CAUTION_BLOCK.asItem()));
            }, false);
        }, 100);

    }
}