package com.chyzman.chowl.core.multipart.pond;

import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.Tuple;

import java.util.Map;

public interface BakingBufferSource {
    Map<RenderType, MeshData> chowl$bakeAllBatches();
    Map<RenderType, MeshData> chowl$bakeBatch();
}
