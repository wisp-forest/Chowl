package com.chyzman.chowl.registry.client;

import com.chyzman.chowl.block.BlockButtonProvider;
import com.chyzman.chowl.block.DrawerFrameBlock;
import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.item.BasePanelItem;
import com.chyzman.chowl.item.PanelTooltipComponent;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class ClientEventListeners {
    public static void init() {
        WorldRenderEvents.BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {
            if (worldRenderContext.world().getBlockEntity(blockOutlineContext.blockPos()) instanceof DrawerFrameBlockEntity) {
                return false;
            }
            return true;
        });

        TooltipComponentCallback.EVENT.register(
                data -> data instanceof BasePanelItem.TooltipData tooltipData
                        ? new PanelTooltipComponent(tooltipData.stack())
                        : null
        );
    }
}