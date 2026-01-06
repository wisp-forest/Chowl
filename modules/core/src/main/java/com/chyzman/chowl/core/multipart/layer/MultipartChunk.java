package com.chyzman.chowl.core.multipart.layer;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.ticks.LevelChunkTicks;

public class MultipartChunk extends LevelChunk {
    private LevelChunk mainChunk;

    public MultipartChunk(
      MultipartLayer layer,
      LevelChunk mainChunk,
      ChunkPos pos
    ) {
        super(layer, pos, UpgradeData.EMPTY, new LevelChunkTicks<>(), new LevelChunkTicks<>(), 0L, null, null, null);
        this.mainChunk = mainChunk;
    }

    @Override
    public void runPostLoad() {}
}
