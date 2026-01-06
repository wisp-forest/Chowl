package com.chyzman.chowl.core.multipart.mixin;

import com.chyzman.chowl.core.multipart.layer.MultipartChunk;
import com.chyzman.chowl.core.multipart.pond.LayerChunkHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkAccess.class)
public class ChunkMixin implements LayerChunkHolder {
    private MultipartChunk multipartChunk;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void loadMultipartChunk(ChunkPos pos, UpgradeData upgradeData, LevelHeightAccessor heightLimitView, PalettedContainerFactory palettesFactory, long inhabitedTime, LevelChunkSection[] sectionArray, BlendingData blendingData, CallbackInfo ci) {
        if ((Object) this instanceof MultipartChunk) return;

        //multipartChunk = new MultipartChunk((Chunk) (Object) this, pos, heightLimitView, biomeRegistry, inhabitedTime);
    }

    @Override
    public MultipartChunk chowl$getMultipartLayer() {
        return multipartChunk;
    }
}
