package com.chyzman.chowl.electromechanics.registry;

import com.chyzman.chowl.electromechanics.Electromechanics;
import com.chyzman.chowl.electromechanics.block.KnockerBlock;
import com.chyzman.chowl.electromechanics.block.WatcherBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

import static net.minecraft.block.Blocks.*;

public class ElectromechanicsBlocks {

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

    private static Block register(String id, Block.Settings settings, Function<AbstractBlock.Settings, Block> factory) {
        return Blocks.register(RegistryKey.of(RegistryKeys.BLOCK, Electromechanics.id(id)), factory, settings);
    }

    public static void init() {
    }
}
