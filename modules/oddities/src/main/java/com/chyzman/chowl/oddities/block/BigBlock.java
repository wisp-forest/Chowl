package com.chyzman.chowl.oddities.block;

import com.chyzman.chowl.core.megaBlock.api.MegaBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;

public class BigBlock extends MegaBlock {

    public static final IntProperty X = IntProperty.of("x", 0, 7);
    public static final IntProperty Y = IntProperty.of("y", 0, 7);
    public static final IntProperty Z = IntProperty.of("z", 0, 7);

    public BigBlock(Settings settings) {
        super(X, Y, Z, settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(X, Y, Z);
    }
}
