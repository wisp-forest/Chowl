package com.chyzman.chowl.core.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.chyzman.chowl.core.ChowlCore.id;

public class ChowlCoreClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
    }

    public static void reloadPos(World world, BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (world == client.world) {
            client.worldRenderer.scheduleBlockRender(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
        }
    }
}
