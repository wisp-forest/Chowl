package com.chyzman.chowl.registry.client;

import com.chyzman.chowl.block.BlockButtonProvider;
import com.chyzman.chowl.block.DrawerFrameBlock;
import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class ClientEventListeners {
    public static void init() {
//        WorldRenderEvents.BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {
//            if (worldRenderContext.world().getBlockEntity(blockOutlineContext.blockPos()) instanceof DrawerFrameBlockEntity blockEntity) {
//                if (blockEntity.getCachedState().getBlock() instanceof BlockButtonProvider buttonProvider) {
//                    var ray = blockOutlineContext.entity().raycast(100, 0, false);
//                    if (ray instanceof BlockHitResult blockHitResult) {
//                        BlockButtonProvider.Button button = buttonProvider.findButton(worldRenderContext.world(), blockOutlineContext.blockState(), blockHitResult);
//                        if (button != null) {
//                            VoxelShape shape = VoxelShapes.cuboid(button.minX()*16, button.minY()*16, 0, button.maxX()*16, button.maxY()*16, 1);
//                            WorldRenderer.drawShapeOutline(worldRenderContext.matrixStack(), blockOutlineContext.vertexConsumer(), shape, 0, 0.0F, 0.0F, 0.0F, 0.4F, 0.0F, 0.0F, true);
//                        } else {
//                            WorldRenderer.drawShapeOutline(worldRenderContext.matrixStack(), blockOutlineContext.vertexConsumer(), DrawerFrameBlock.BASE, 0, 0.0F, 0.0F, 0.0F, 0.4F, 0.0F, 0.0F, true);
//                        }
//                        return false;
//                    }
                    //TODO fix this i got no idea what the fuck
//                }
//            }
//            return true;
//        });
    }
}