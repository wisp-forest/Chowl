package com.chyzman.chowl.core.blockentity;

import com.chyzman.chowl.core.graph.NetworkMember;
import com.chyzman.chowl.core.graph.node.PanelHolderNode;
import com.chyzman.chowl.core.registry.CoreBlockEntities;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class FrameBlockEntity extends MultipartBlockEntity implements NetworkMember {
    public FrameBlockEntity(BlockPos pos, BlockState state) {
        super(CoreBlockEntities.FRAME, pos, state);
    }

    @Override
    public List<BlockNode> getNodes() {
        List<BlockNode> list = new ArrayList<>();

        list.add(PanelHolderNode.INSTANCE);

        for (var part : getParts()) {
            if (part instanceof NetworkMember member)
                list.addAll(member.getNodes());
        }

        return list;
    }
}
