package com.chyzman.chowl.visage.client;

import com.chyzman.chowl.industries.Chowl;
import com.chyzman.chowl.industries.util.InfallibleCloseable;
import com.chyzman.chowl.visage.block.VisageBlockEntity;

public class RenderGlobals {
    public static final ThreadLocal<VisageBlockEntity> VISAGE = new ThreadLocal<>();

    public static int RECURSION_COUNTER = 0;

    public static boolean shouldRender() {
        return RECURSION_COUNTER < Chowl.CHOWL_CONFIG.recursive_rendering_limit();
    }

    public static InfallibleCloseable enterRender() {
        RECURSION_COUNTER += 1;

        return () -> RECURSION_COUNTER -= 1;
    }
}
