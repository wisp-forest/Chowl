//package com.chyzman.chowl.industries.registry;
//
//import com.chyzman.chowl.core.block.FrameBlock;
//import com.chyzman.chowl.core.registry.ChowlRegistry;
//import com.chyzman.chowl.industries.Industries;
//import net.minecraft.block.AbstractBlock;
//import net.minecraft.block.Block;
//import net.minecraft.block.Blocks;
//import net.minecraft.registry.Registries;
//import net.minecraft.registry.Registry;
//import net.minecraft.registry.RegistryKey;
//import net.minecraft.registry.RegistryKeys;
//
//import java.util.function.Function;
//
//public class IndustryBlocks implements ChowlRegistry {
//
//    public static final Block DRAWER_FRAME = register(
//            "drawer_frame",
//            new FrameBlock(
//                    AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
//                            .nonOpaque()
//                            .dynamicBounds()
//                            .allowsSpawning(Blocks::never)
//                            .solidBlock(Blocks::never)
//                            .suffocates(Blocks::never)
//                            .blockVision(Blocks::never)
////                          .luminance(FrameBlock.STATE_TO_LUMINANCE)
//            )
//    );
//
//    private static Block register(String id, Block block) {
//        return Registry.register(Registries.BLOCK, Industries.id(id), block);
//    }
//}
