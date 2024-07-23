package com.chyzman.chowl.core.pond;

import net.minecraft.util.math.BlockPos;

public class MixinState {
    public static final ThreadLocal<BlockPos> STEP_POS = new ThreadLocal<>();
    public static final ThreadLocal<BlockPos> SECONDARY_STEP_POS = new ThreadLocal<>();
}