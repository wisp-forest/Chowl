package com.chyzman.chowl.industries.client;

import com.chyzman.chowl.industries.Chowl;
import com.chyzman.chowl.industries.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.industries.util.InfallibleCloseable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

public class RenderGlobals {
    public static final ThreadLocal<DrawerFrameBlockEntity> DRAWER_FRAME = new ThreadLocal<>();
    public static final ThreadLocal<Direction> FRAME_SIDE = new ThreadLocal<>();
    public static final ThreadLocal<BlockPos> FRAME_POS = new ThreadLocal<>();
    public static final ThreadLocal<BlockRenderView> FRAME_WORLD = new ThreadLocal<>();
    public static final ThreadLocal<Boolean> BAKED = new ThreadLocal<>();

    public static boolean IN_FRAME = false;
    public static int RECURSION_COUNTER = 0;

    public static boolean shouldRender() {
        return RECURSION_COUNTER < Chowl.CHOWL_CONFIG.recursive_rendering_limit();
    }

    public static InfallibleCloseable enterRender() {
        RECURSION_COUNTER += 1;

        return () -> RECURSION_COUNTER -= 1;
    }
}