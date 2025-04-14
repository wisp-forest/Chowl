package com.chyzman.chowl.core.blockButtons;

import net.minecraft.util.shape.VoxelShape;

public class BlockButton {
    private VoxelShape shape;

    public BlockButton(VoxelShape shape) {
        this.shape = shape;
    }

    public VoxelShape getShape() {
        return shape;
    }
}
