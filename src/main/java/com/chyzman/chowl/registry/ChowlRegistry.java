package com.chyzman.chowl.registry;

import com.chyzman.chowl.block.DrawerFrameBlock;
import com.chyzman.chowl.criteria.InsertedUpgradeCriteria;
import com.chyzman.chowl.criteria.LabeledPanelCriteria;
import com.chyzman.chowl.criteria.WitnessedBlastingCriteria;
import com.chyzman.chowl.item.*;
import io.wispforest.lavender.book.LavenderBookItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.Stats;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

public class ChowlRegistry {
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, id(name), item);
    }

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, id(name), block);
    }

    private static Identifier registerCustomStat(String name) {
        Identifier stat = id(name);

        Registry.register(Registries.CUSTOM_STAT, stat, stat);
        Stats.CUSTOM.getOrCreateStat(stat);

        return stat;
    }

    public static final Item CHOWL_HANDBOOK = LavenderBookItem.registerForBook(id("chowl_handbook"), new Item.Settings().maxCount(1));

    public static final Item DRAWER_PANEL_ITEM = registerItem("drawer_panel", new DrawerPanelItem(new Item.Settings().maxCount(1)));

    public static final Item ACCESS_PANEL_ITEM = registerItem("access_panel", new AccessPanelItem(new Item.Settings().maxCount(1)));

    public static final Item MIRROR_PANEL_ITEM = registerItem("mirror_panel", new MirrorPanelItem(new Item.Settings().maxCount(1)));

    public static final Item PHANTOM_PANEL_ITEM = registerItem("phantom_panel", new BlankPanelItem(new Item.Settings()));

    public static final Item COMPRESSING_PANEL_ITEM = registerItem("compressing_panel", new CompressingPanelItem(new Item.Settings().maxCount(1)));

    public static final Block DRAWER_FRAME_BLOCK = registerBlock("drawer_frame",
            new DrawerFrameBlock(
                    AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
                            .nonOpaque()
                            .dynamicBounds()
                            .allowsSpawning(Blocks::never)
                            .solidBlock(Blocks::never)
                            .suffocates(Blocks::never)
                            .blockVision(Blocks::never)
                            .luminance(DrawerFrameBlock.STATE_TO_LUMINANCE)
            )
    );
    public static final Item DRAWER_FRAME_ITEM = registerItem("drawer_frame", new DrawerFrameBlockItem(DRAWER_FRAME_BLOCK, new Item.Settings()));

    public static final Block CAUTION_BLOCK = registerBlock("caution_block", new Block(FabricBlockSettings.copy(Blocks.IRON_BLOCK).mapColor(DyeColor.YELLOW)));
    public static final Item CAUTION_BLOCK_ITEM = registerItem("caution_block", new BlockItem(CAUTION_BLOCK, new Item.Settings()));

    public static final WitnessedBlastingCriteria WITNESSED_BLASTING_CRITERIA = Criteria.register(WitnessedBlastingCriteria.ID.toString(), new WitnessedBlastingCriteria());
    public static final LabeledPanelCriteria LABELED_PANEL_CRITERIA = Criteria.register(LabeledPanelCriteria.ID.toString(), new LabeledPanelCriteria());
    public static final InsertedUpgradeCriteria INSERTED_UPGRADE_CRITERIA = Criteria.register(InsertedUpgradeCriteria.ID.toString(), new InsertedUpgradeCriteria());

    public static final Identifier ITEMS_INSERTED_STAT = registerCustomStat("items_inserted");
    public static final Identifier ITEMS_EXTRACTED_STAT = registerCustomStat("items_extracted");

    public static void init() {
    }
}