package com.chyzman.chowl.core.graph;

import com.chyzman.chowl.core.graph.cache.NetworkStorageCache;
import com.chyzman.chowl.core.graph.cache.SimpleNetworkStorageCache;
import com.kneelawk.graphlib.api.graph.GraphUniverse;
import com.kneelawk.graphlib.api.graph.user.GraphEntityType;
import com.kneelawk.graphlib.api.world.SaveMode;
import com.mojang.serialization.Codec;

import static com.chyzman.chowl.core.ChowlCore.id;

public class NetworkRegistry {
    public static final GraphUniverse UNIVERSE = GraphUniverse.builder()
            .saveMode(SaveMode.INCREMENTAL)
            .build(id("networks"));

    public static final GraphEntityType<NetworkStorageCache> STORAGE_CACHE_TYPE = GraphEntityType.of(id("storage_cache"),
            Codec.unit(SimpleNetworkStorageCache::new),
            SimpleNetworkStorageCache::new,
            NetworkStorageCache::split);

    public static final GraphEntityType<UpdateHandler> UPDATE_HANDLER_TYPE = GraphEntityType.of(id("update_handler"), UpdateHandler::new);



    public static void init() {
        UNIVERSE.register();
        UNIVERSE.addDiscoverer();

        UNIVERSE.addGraphEntityTypes(STORAGE_CACHE_TYPE, UPDATE_HANDLER_TYPE);
    }
}
