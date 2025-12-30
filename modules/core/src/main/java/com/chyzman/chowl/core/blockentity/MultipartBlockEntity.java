package com.chyzman.chowl.core.blockentity;

import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.graph.NetworkMember;
import com.chyzman.chowl.core.registry.CoreBlockEntities;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createNbt(registries);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
