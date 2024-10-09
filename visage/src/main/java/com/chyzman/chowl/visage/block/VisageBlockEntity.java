package com.chyzman.chowl.visage.block;

import com.chyzman.chowl.core.blockentity.api.TemplatableBlockEntity;
import com.chyzman.chowl.visage.registry.VisageBlocks;
import io.wispforest.owo.ops.WorldOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class VisageBlockEntity extends TemplatableBlockEntity {
    public VisageBlockEntity(BlockPos pos, BlockState state) {
        super(VisageBlocks.Entities.VISAGE_BLOCK, pos, state);
    }

    protected VisageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void scheduleSpreadTemplate() {
        world.scheduleBlockTick(pos, getCachedState().getBlock(), 1);
    }

    public void spreadTemplate() {
        for (int i = 0; i < 6; i++) {
            var possible = pos.offset(Direction.byId(i));

            if (!(world.getBlockEntity(possible) instanceof VisageBlockEntity other)) continue;
            if (other.templateState() == templateState()) continue;

            other.setTemplateState(templateState());
            other.scheduleSpreadTemplate();
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        WorldOps.updateIfOnServer(world, pos);
    }
}
