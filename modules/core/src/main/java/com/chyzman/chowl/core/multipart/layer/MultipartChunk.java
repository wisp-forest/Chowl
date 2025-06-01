package com.chyzman.chowl.core.multipart.layer;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.tick.ChunkTickScheduler;

public class MultipartChunk extends WorldChunk {
    private WorldChunk mainChunk;

    public MultipartChunk(
      MultipartLayer layer,
      WorldChunk mainChunk,
      ChunkPos pos
    ) {
        super(layer, pos, UpgradeData.NO_UPGRADE_DATA, new ChunkTickScheduler<>(), new ChunkTickScheduler<>(), 0L, null, null, null);
        this.mainChunk = mainChunk;
    }

    @Override
    public void loadEntities() {}
}
