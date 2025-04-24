package com.chyzman.chowl.electromechanics.client;

import com.chyzman.chowl.electromechanics.registry.ElectromechanicsBlockEntities;
import com.chyzman.chowl.electromechanics.registry.ElectromechanicsEventListeners;
import net.fabricmc.api.ClientModInitializer;

public class ElectromechanicsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ElectromechanicsBlockEntities.clientInit();

        ElectromechanicsEventListeners.clientInit();
    }
}
