package com.chyzman.chowl.core.blockentity.api;

import com.chyzman.chowl.core.graph.NetworkMember;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class NetworkBlockEntity extends BlockEntity implements NetworkMember {
    public NetworkBlockEntity(BlockEntityType<? extends MultipartHolderBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
