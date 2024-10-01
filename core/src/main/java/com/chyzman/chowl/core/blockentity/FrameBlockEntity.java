package com.chyzman.chowl.core.blockentity;

import com.chyzman.chowl.core.graph.NetworkRegistry;
import com.chyzman.chowl.core.graph.UpdateHandler;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.stream.Stream;

public abstract class FrameBlockEntity extends TemplatableBlockEntity {
    public FrameBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void onSlotChanged(boolean sortingChanged) {
        if (world instanceof ServerWorld serverWorld) {
            world.getWorldChunk(pos).setNeedsSaving(true);
            UpdateHandler.scheduleUpdate(serverWorld, pos, sortingChanged ? UpdateHandler.ChangeType.CONTENT : UpdateHandler.ChangeType.COUNT);
            var state = getCachedState();
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            world.updateComparators(pos, getCachedState().getBlock());
        }
    }

    //TODO look into this later
    public abstract Stream<? extends Storage> streamStorages();

    @Override
    public abstract void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);

    @Override
    public void markRemoved() {
        super.markRemoved();
        if (world instanceof ServerWorld serverWorld) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld)
                    .getAllGraphsAt(pos)
                    .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                    .forEach(cache -> cache.onNodeUnloaded(pos));
        }
    }

    @Override
    public void cancelRemoval() {
        super.cancelRemoval();
        if (world instanceof ServerWorld serverWorld) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld)
                    .getAllGraphsAt(pos)
                    .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                    .forEach(cache -> cache.onNodeReloaded(pos));
        }
    }
}
