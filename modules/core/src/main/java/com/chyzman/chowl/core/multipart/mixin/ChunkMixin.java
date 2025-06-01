package com.chyzman.chowl.core.multipart.mixin;

import com.chyzman.chowl.core.multipart.layer.MultipartChunk;
import com.chyzman.chowl.core.multipart.pond.LayerChunkHolder;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.chunk.BlendingData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public class ChunkMixin implements LayerChunkHolder {
    private MultipartChunk multipartChunk;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void loadMultipartChunk(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry<Biome> biomeRegistry, long inhabitedTime, ChunkSection[] sectionArray, BlendingData blendingData, CallbackInfo ci) {
        if ((Object) this instanceof MultipartChunk) return;

        //multipartChunk = new MultipartChunk((Chunk) (Object) this, pos, heightLimitView, biomeRegistry, inhabitedTime);
    }

    @Override
    public MultipartChunk chowl$getMultipartLayer() {
        return multipartChunk;
    }
}
