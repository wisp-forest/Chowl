package com.chyzman.chowl.core.graph.cache;

import com.chyzman.chowl.core.graph.NetworkRegistry;
import com.kneelawk.graphlib.api.graph.BlockGraph;
import com.kneelawk.graphlib.api.graph.user.GraphEntity;
import com.kneelawk.graphlib.api.graph.user.GraphEntityType;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface NetworkStorageCache extends GraphEntity<NetworkStorageCache> {

    static CombinedStorage<ItemVariant, DrawerStorage> get(ServerWorld world, BlockPos pos) {
        return NetworkRegistry.UNIVERSE.getGraphWorld(world)
                .getLoadedGraphsAt(pos)
                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                .map(NetworkStorageCache::get)
                .findFirst()
                .orElseGet(() -> new CombinedStorage<>(new ArrayList<>()));
    }

    public static class DummyStorage {
        private final Map<Class<? extends TransferVariant<?>>, CombinedStorage<? extends TransferVariant<?>, ? extends Storage<? extends TransferVariant<?>>>> cachedStorage = new HashMap<>();

        @Nullable
        public <T, V extends TransferVariant<T>, S extends Storage<V>> CombinedStorage<V, S> getStorage(Class<V> variantClass, Class<S> storageClass) {
            return (CombinedStorage<V, S>) map.get(variantClass);
        }

        public <T, V extends TransferVariant<T>, S extends Storage<V>> void setStorage(Class<V> variantClass, CombinedStorage<V, S> storage) {
            map.put(variantClass, storage);
        }
    }

    @Nullable <T, V extends TransferVariant<T>, S extends Storage<V>> CombinedStorage<V, S> get(Class<V> variantClass, Class<S> storageClass);

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
