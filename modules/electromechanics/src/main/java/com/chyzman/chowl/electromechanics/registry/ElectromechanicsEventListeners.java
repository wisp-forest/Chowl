package com.chyzman.chowl.electromechanics.registry;

import com.chyzman.chowl.electromechanics.block.KnockerBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ElectromechanicsEventListeners {
    public static void init() {
        KnockerBlock.initEventListeners();
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {

    }
}
