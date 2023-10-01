package com.chyzman.chowl.registry.client;

import com.chyzman.chowl.graph.ClientGraphStore;
import com.chyzman.chowl.graph.DestroyGraphPacket;
import com.chyzman.chowl.graph.SyncGraphPacket;
import com.chyzman.chowl.network.C2SConfigPanel;
import com.chyzman.chowl.screen.PanelConfigScreen;
import net.minecraft.client.MinecraftClient;

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