package com.chyzman.chowl.oddities.registry;

import com.chyzman.chowl.oddities.block.KnockerBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class OdditiesEventListeners {
    public static void init() {
        KnockerBlock.initEventListeners();
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {

    }
}
