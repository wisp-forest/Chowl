package com.chyzman.chowl.core.blockentity;

import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.graph.NetworkMember;
import com.chyzman.chowl.core.registry.CoreBlockEntities;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MultipartBlockEntity extends MultipartHolderBlockEntity implements NetworkMember {
    public MultipartBlockEntity(BlockEntityType<? extends MultipartBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public MultipartBlockEntity(BlockPos pos, BlockState state) {
        this(CoreBlockEntities.MULTIPART, pos, state);
    }

    @Override
    public List<BlockNode> getNodes() {
        return List.of();
    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
