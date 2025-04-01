package com.chyzman.chowl.oddities.client;

import com.chyzman.chowl.oddities.registry.OdditiesAttachables;
import com.chyzman.chowl.oddities.registry.OdditiesBlockEntities;
import com.chyzman.chowl.oddities.registry.OdditiesEventListeners;
import net.fabricmc.api.ClientModInitializer;

public class OdditiesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OdditiesBlockEntities.clientInit();

        OdditiesEventListeners.clientInit();

        OdditiesAttachables.clientInit();
    }
}
