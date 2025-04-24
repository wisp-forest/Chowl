package com.chyzman.chowl.core.blockentity.api;

import com.chyzman.chowl.core.graph.NetworkMember;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public abstract class NetworkBlockEntity extends MultipartBlockEntity implements NetworkMember {
    public NetworkBlockEntity(BlockEntityType<? extends MultipartBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
