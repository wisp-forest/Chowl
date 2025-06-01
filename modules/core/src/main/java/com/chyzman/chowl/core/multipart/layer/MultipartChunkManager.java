package com.chyzman.chowl.core.multipart.layer;

import com.chyzman.chowl.core.multipart.pond.LayerChunkHolder;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class MultipartChunkManager extends ChunkManager {
    private final ChunkManager mainManager;

    public MultipartChunkManager(ChunkManager mainManager) {
        this.mainManager = mainManager;
    }

    @Override
    public @Nullable Chunk getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
        Chunk chunk = mainManager.getChunk(x, z, leastStatus, create);
        if (chunk == null) return null;

        return ((LayerChunkHolder) chunk).chowl$getMultipartLayer();
    }

    @Override
    public void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {}

    @Override
    public String getDebugString() {
        return "";
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public LightingProvider getLightingProvider() {
        return null;
    }

    @Override
    public BlockView getWorld() {
        return null;
    }
}
