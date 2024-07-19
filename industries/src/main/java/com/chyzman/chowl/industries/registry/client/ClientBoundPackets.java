package com.chyzman.chowl.industries.registry.client;

import com.chyzman.chowl.industries.graph.ClientGraphStore;
import com.chyzman.chowl.industries.graph.DestroyGraphPacket;
import com.chyzman.chowl.industries.graph.SyncGraphPacket;

import static com.chyzman.chowl.industries.Chowl.CHANNEL;

public class ClientBoundPackets {
    public static void init() {
        CHANNEL.registerClientbound(SyncGraphPacket.class, (message, access) -> {
            ClientGraphStore.STORE.insert(message);
        });

        CHANNEL.registerClientbound(DestroyGraphPacket.class, (message, access) -> {
            ClientGraphStore.STORE.remove(message.graphId());
        });
    }
}