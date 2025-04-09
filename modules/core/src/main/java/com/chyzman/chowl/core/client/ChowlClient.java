package com.chyzman.chowl.core.client;

import com.chyzman.chowl.core.attachable.client.AttachableRendererEvents;
import com.chyzman.chowl.core.network.ChowlPackets;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChowlClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ChowlPackets.registerClient();
        AttachableRendererEvents.init();
    }

    public static void reloadPos(World world, BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (world == client.world) {
            client.worldRenderer.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
