package com.chyzman.chowl.core.graph.node;

import com.chyzman.chowl.core.Chowl;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import com.kneelawk.graphlib.api.wire.SidedFaceBlockNode;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record PanelNode(Direction direction) implements SidedFaceBlockNode {
    public static final Identifier ID = Chowl.id("panel");
    public static final BlockNodeType TYPE = BlockNodeType.of(ID, Direction.CODEC.xmap(PanelNode::new, PanelNode::getSide));

    @Override
    public @NotNull BlockNodeType getType() {
        return TYPE;
    }

    @Override
    public void onConnectionsChanged(@NotNull NodeHolder<BlockNode> self) {
    }

    @Override
    public @NotNull Direction getSide() {
        return direction;
    }
}
