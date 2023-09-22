package com.chyzman.chowl.client;

import com.chyzman.chowl.block.DrawerFrameBlockEntityRenderer;
import com.chyzman.chowl.registry.ChowlRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

import static com.chyzman.chowl.Chowl.DRAWER_FRAME_BLOCK_ENTITY_TYPE;

public class ChowlClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(DRAWER_FRAME_BLOCK_ENTITY_TYPE, DrawerFrameBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ChowlRegistry.DRAWER_FRAME, RenderLayer.getCutout());
    }
}