package com.chyzman.chowl.core.client;

import com.chyzman.chowl.core.attachables.impl.AttachablesEvents;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderManager;
import com.chyzman.chowl.core.client.render.block.entity.MultipartBlockEntityRenderer;
import com.chyzman.chowl.core.network.ChowlPackets;
import com.chyzman.chowl.core.panel.TemporaryPanelInitThingy;
import com.chyzman.chowl.core.registry.CoreBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ChowlClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ChowlPackets.registerClient();
        AttachablesEvents.clientInit();

        BlockEntityRenderers.register(CoreBlockEntities.MULTIPART, MultipartBlockEntityRenderer::new);
        BlockEntityRenderers.register(CoreBlockEntities.FRAME, MultipartBlockEntityRenderer::new);

        WorldRenderEvents.END_EXTRACTION.register(PartRenderManager::extract);
        WorldRenderEvents.AFTER_ENTITIES.register(PartRenderManager::submit);
        WorldRenderEvents.AFTER_ENTITIES.register(PartRenderManager::render);
        InvalidateRenderStateCallback.EVENT.register(PartRenderManager::reset);

        TemporaryPanelInitThingy.clientInit();
    }

    public static void reloadPos(Level world, BlockPos pos) {
        Minecraft client = Minecraft.getInstance();

        if (world == client.level) {
            client.levelRenderer.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }


    }
}
