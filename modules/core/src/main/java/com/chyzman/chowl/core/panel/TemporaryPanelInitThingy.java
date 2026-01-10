package com.chyzman.chowl.core.panel;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.panel.registry.PanelComponents;
import com.chyzman.chowl.core.panel.registry.PanelParts;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class TemporaryPanelInitThingy {

    public static void init() {
        PanelParts.init();
        FieldRegistrationHandler.register(PanelComponents.class, Chowl.MODID, true);
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {

    }
}
