package com.chyzman.chowl.core.multipart.layer;

import com.chyzman.chowl.core.multipart.pond.LayerChunkHolder;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class MultipartChunkManager extends ChunkSource {
    private final ChunkSource mainManager;

    public MultipartChunkManager(ChunkSource mainManager) {
        this.mainManager = mainManager;
    }

    @Override
    public @Nullable ChunkAccess getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
        ChunkAccess chunk = mainManager.getChunk(x, z, leastStatus, create);
        if (chunk == null) return null;

        return ((LayerChunkHolder) chunk).chowl$getMultipartLayer();
    }

    @Override
    public void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {}

    @Override
    public String gatherStats() {
        return "";
    }

    @Override
    public int getLoadedChunksCount() {
        return 0;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return null;
    }

    @Override
    public BlockGetter getLevel() {
        return null;
    }
}
