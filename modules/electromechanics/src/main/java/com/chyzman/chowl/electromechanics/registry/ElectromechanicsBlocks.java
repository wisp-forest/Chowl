package com.chyzman.chowl.electromechanics.registry;

import com.chyzman.chowl.electromechanics.Electromechanics;
import com.chyzman.chowl.electromechanics.block.KnockerBlock;
import com.chyzman.chowl.electromechanics.block.WatcherBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

import static net.minecraft.world.level.block.Blocks.OBSERVER;

public class ElectromechanicsBlocks {

    public static final Block WATCHER = register(
        "watcher",
        BlockBehaviour.Properties
                    .ofFullCopy(OBSERVER),
        WatcherBlock::new
    );

    public static final Block KNOCKER = register(
        "knocker",
        BlockBehaviour.Properties
                    .ofFullCopy(OBSERVER),
        KnockerBlock::new
    );

    private static Block register(String id, BlockBehaviour.Properties settings, Function<BlockBehaviour.Properties, Block> factory) {
        return Blocks.register(ResourceKey.create(Registries.BLOCK, Electromechanics.id(id)), factory, settings);
    }

    public static void init() {
    }
}
