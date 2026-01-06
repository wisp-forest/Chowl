package com.chyzman.chowl.core.registry;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.block.FrameBlock;
import com.chyzman.chowl.core.block.api.FluidFillHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CoreBlocks {
    public static final Block DRAWER_FRAME = register(
        "drawer_frame",
        FrameBlock::new,
        //TODO: changed copyshallow to copy, make sure this is ok
        BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .noOcclusion()
            .dynamicShape()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor(Blocks::never)
            .isSuffocating(Blocks::never)
            .isViewBlocking(Blocks::never)
//      .luminance(FrameBlock.STATE_TO_LUMINANCE)
    );

    private static Block register(String id, BlockFactory factory, BlockBehaviour.Properties settings) {
        Identifier chowlId = Chowl.id(id);
        Block block = Registry.register(BuiltInRegistries.BLOCK, chowlId, factory.create(settings.setId(ResourceKey.create(Registries.BLOCK, chowlId))));
        Items.registerBlock(block);
        return block;
    }

    private static Block registerWithoutItem(String id, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, Chowl.id(id), block);
    }

    public static void init() {
        FluidFillHandler.canNotFill(state -> state.getBlock() instanceof FrameBlock);
    }

    @FunctionalInterface
    interface BlockFactory {
        Block create(BlockBehaviour.Properties properties);
    }
}
