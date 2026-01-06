package com.chyzman.chowl.test.registry;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.test.ChowlTest;
import com.chyzman.chowl.test.block.TestMultipartBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TestBlocks {
    public static final Block TEST_BLOCK = register(
      "test_block",
      TestMultipartBlock::new,
      BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
        .noOcclusion()
        .dynamicShape()
        .isValidSpawn(Blocks::never)
        .isRedstoneConductor(Blocks::never)
        .isSuffocating(Blocks::never)
        .isViewBlocking(Blocks::never)
//      .luminance(FrameBlock.STATE_TO_LUMINANCE)
    );

    private static Block register(String id, BlockFactory factory, BlockBehaviour.Properties properties) {
        Identifier chowlId = ChowlTest.id(id);
        Block block = Registry.register(BuiltInRegistries.BLOCK, chowlId, factory.create(properties.setId(ResourceKey.create(Registries.BLOCK, chowlId))));
        Registry.register(BuiltInRegistries.ITEM, chowlId, new BlockItem(block, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, chowlId))));
        return block;
    }

    private static Block registerWithoutItem(String id, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, Chowl.id(id), block);
    }

    public static void init() {
    }

    @FunctionalInterface
    interface BlockFactory {
        Block create(BlockBehaviour.Properties properties);
    }
}
