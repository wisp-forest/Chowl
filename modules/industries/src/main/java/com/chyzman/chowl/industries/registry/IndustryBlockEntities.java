//package com.chyzman.chowl.industries.registry;
//
//import com.chyzman.chowl.core.registry.ChowlRegistry;
//import com.chyzman.chowl.industries.Industries;
//import com.chyzman.chowl.industries.blockentity.FrameBlockEntity;
//import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.registry.Registries;
//import net.minecraft.registry.Registry;
//
//public class IndustryBlockEntities implements ChowlRegistry {
//
//    public static final BlockEntityType<FrameBlockEntity> DRAWER_FRAME =
//            register(
//                    "drawer_frame",
//                    FabricBlockEntityTypeBuilder.create(
//                            FrameBlockEntity::new,
//                            IndustryBlocks.DRAWER_FRAME
//                    )
//            );
//
//    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder<T> builder) {
//        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Industries.id(id), builder.build());
//
//    }
//}
