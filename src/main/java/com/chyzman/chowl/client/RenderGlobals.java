package com.chyzman.chowl.client;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
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
}