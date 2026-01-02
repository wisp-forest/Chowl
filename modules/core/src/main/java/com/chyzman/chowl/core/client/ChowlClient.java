package com.chyzman.chowl.core.client;

import com.chyzman.chowl.core.attachables.impl.AttachablesEvents;
import com.chyzman.chowl.core.multipart.api.client.PartRenderer;
import com.chyzman.chowl.core.client.render.block.entity.MultipartBlockEntityRenderer;
import com.chyzman.chowl.core.network.ChowlPackets;
import com.chyzman.chowl.core.registry.CoreBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChowlClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ChowlPackets.registerClient();
        AttachablesEvents.clientInit();

        BlockEntityRendererFactories.register(CoreBlockEntities.MULTIPART, MultipartBlockEntityRenderer::new);

        WorldRenderEvents.AFTER_ENTITIES.register(PartRenderer.Manager::render);
        InvalidateRenderStateCallback.EVENT.register(PartRenderer.Manager::reset);
    }

    public static void reloadPos(World world, BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (world == client.world) {
            client.worldRenderer.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }


    }
}
