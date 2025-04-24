package com.chyzman.chowl.core.blockentity;

import com.chyzman.chowl.core.blockentity.api.PanelHolderBlockEntity;
import com.chyzman.chowl.core.graph.node.PanelHolderNode;
import com.chyzman.chowl.core.graph.node.PanelNode;
import com.chyzman.chowl.core.registry.CoreBlockEntities;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class FrameBlockEntity extends PanelHolderBlockEntity {
    public FrameBlockEntity(BlockPos pos, BlockState state) {
        super(CoreBlockEntities.DRAWER_FRAME, pos, state);
    }

    @Override
    public List<BlockNode> getNodes() {
        var returned = new ArrayList<BlockNode>();
        returned.add(PanelHolderNode.INSTANCE);
        for (Direction value : Direction.values()) {
            returned.add(new PanelNode(value));
        }
        return returned;
    }
}
