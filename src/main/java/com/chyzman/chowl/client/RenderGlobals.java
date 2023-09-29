package com.chyzman.chowl.client;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import net.minecraft.util.math.Direction;

public class RenderGlobals {
    public static final ThreadLocal<DrawerFrameBlockEntity> DRAWER_FRAME = new ThreadLocal<>();
    public static final ThreadLocal<Direction> FRAME_SIDE = new ThreadLocal<>();
}
