package com.chyzman.chowl.core.graph.cache;

import com.chyzman.chowl.core.graph.NetworkRegistry;
import com.kneelawk.graphlib.api.graph.BlockGraph;
import com.kneelawk.graphlib.api.graph.user.GraphEntity;
import com.kneelawk.graphlib.api.graph.user.GraphEntityType;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface NetworkStorageCache extends GraphEntity<NetworkStorageCache> {

    static <T, V extends TransferVariant<T>, S extends Storage<V>> CombinedStorage<V, S> get(Class<V> variantClass, Class<S> storageClass, ServerWorld world, BlockPos pos) {
        return NetworkRegistry.UNIVERSE.getGraphWorld(world)
                .getLoadedGraphsAt(pos)
                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                .map(networkStorageCache -> networkStorageCache.get(variantClass, storageClass))
                .findFirst()
                .orElseGet(() -> new CombinedStorage<>(new ArrayList<>()));
    }

    <T, V extends TransferVariant<T>, S extends Storage<V>> CombinedStorage<V, S> get(Class<V> variantClass, Class<S> storageClass);

    void update();

    void forceUpdate();

    void onSortingChanged();

    void onNodeUnloaded(BlockPos pos);

    void onNodeReloaded(BlockPos pos);

    @NotNull NetworkStorageCache split(@NotNull BlockGraph originalGraph, @NotNull BlockGraph newGraph);

    List<Text> getDebugInfo();

    Text getDebugInfo(BlockPos pos);

    @Override
    @NotNull
    default GraphEntityType<?> getType() {
        return NetworkRegistry.STORAGE_CACHE_TYPE;
    }
}
