package com.chyzman.chowl.core.client.util;

import com.chyzman.chowl.core.ChowlCore;
import com.chyzman.chowl.core.util.InfallibleCloseable;

public class RenderCounter {
    public static int RECURSION_COUNTER = 0;

    public static boolean shouldRender() {
        return RECURSION_COUNTER < ChowlCore.CONFIG.recursive_rendering_limit();
    }

    public static InfallibleCloseable enterRender() {
        RECURSION_COUNTER += 1;

        return () -> RECURSION_COUNTER -= 1;
    }
}
