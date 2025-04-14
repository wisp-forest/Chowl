package com.chyzman.chowl.oddities.registry;

import com.chyzman.chowl.oddities.Oddities;
import com.chyzman.chowl.oddities.block.BigBlock;
import com.chyzman.chowl.oddities.block.ClipboardBlock;
import com.chyzman.chowl.oddities.block.KnockerBlock;
import com.chyzman.chowl.oddities.block.WatcherBlock;
import com.chyzman.chowl.oddities.infoDisplay.block.InfoDisplayBlock;
import com.chyzman.chowl.oddities.infoDisplay.block.WallInfoDisplayBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.DyeColor;

import java.util.function.Function;

import static net.minecraft.block.Blocks.*;

public class OdditiesBlocks {

    public static final Block CAUTION_BLOCK = register(
            "caution_block",
            net.minecraft.block.AbstractBlock.Settings
                    .copy(IRON_BLOCK)
                    .mapColor(DyeColor.YELLOW),
            Block::new
    );

    public static final Block CLIPBOARD = register(
            "clipboard",
            AbstractBlock.Settings
                    .copy(OAK_PRESSURE_PLATE),
            ClipboardBlock::new
    );

    public static final Block WATCHER = register(
            "watcher",
            AbstractBlock.Settings
                    .copy(OBSERVER),
            WatcherBlock::new
    );

    public static final Block KNOCKER = register(
            "knocker",
            AbstractBlock.Settings
                    .copy(OBSERVER),
            KnockerBlock::new
    );

    private static final AbstractBlock.Settings infoDisplaySettings = AbstractBlock.Settings
            .copy(IRON_BLOCK)
            .nonOpaque()
            .mapColor(DyeColor.BLACK);

    public static final Block CLOCK = register(
            "clock",
            infoDisplaySettings,
            settings -> new InfoDisplayBlock<>(OdditiesBlockEntities.CLOCK, settings)
    );

    public static final Block WALL_CLOCK = register(
            "wall_clock",
            infoDisplaySettings,
            settings -> new WallInfoDisplayBlock<>(OdditiesBlockEntities.CLOCK, settings)
    );

    public static final Block CALENDAR = register(
            "calendar",
            infoDisplaySettings,
            settings -> new InfoDisplayBlock<>(OdditiesBlockEntities.CALENDAR, settings)
    );

    public static final Block WALL_CALENDAR = register(
            "wall_calendar",
            infoDisplaySettings,
            settings -> new WallInfoDisplayBlock<>(OdditiesBlockEntities.CALENDAR, settings)
    );

    public static final Block BIG_DIRT = register(
            "big_dirt",
            AbstractBlock.Settings
                    .copy(DIRT),
            BigBlock::new
    );

    private static Block register(String id, Block.Settings settings, Function<AbstractBlock.Settings, Block> factory) {
        return Blocks.register(RegistryKey.of(RegistryKeys.BLOCK, Oddities.id(id)), factory, settings);
    }

    public static void init() {
    }
}
