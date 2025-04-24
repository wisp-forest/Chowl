package com.chyzman.chowl.core.block;

import com.chyzman.chowl.core.block.api.MultipartBlockWithEntity;
import com.chyzman.chowl.core.blockentity.FrameBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockWithEntity;

public class FrameBlock extends MultipartBlockWithEntity {
    public static final MapCodec<FrameBlock> CODEC = createCodec(FrameBlock::new);

    public FrameBlock(Settings settings) {
        super(FrameBlockEntity::new, settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
}
