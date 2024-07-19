package com.chyzman.chowl.registry.client;

import com.chyzman.chowl.graph.ClientGraphStore;
import com.chyzman.chowl.graph.DestroyGraphPacket;
import com.chyzman.chowl.graph.SyncGraphPacket;

import static com.chyzman.chowl.Chowl.CHANNEL;

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