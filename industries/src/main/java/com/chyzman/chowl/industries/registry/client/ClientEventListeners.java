package com.chyzman.chowl.industries.registry.client;

import com.chyzman.chowl.industries.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.industries.item.BasePanelItem;
import com.chyzman.chowl.industries.item.PanelTooltipComponent;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class ClientEventListeners {
    public static void init() {
        WorldRenderEvents.BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> !(worldRenderContext.world().getBlockEntity(blockOutlineContext.blockPos()) instanceof DrawerFrameBlockEntity));

        TooltipComponentCallback.EVENT.register(
                data -> data instanceof BasePanelItem.TooltipData tooltipData
                        ? new PanelTooltipComponent(tooltipData.stack())
                        : null
        );
    }
}