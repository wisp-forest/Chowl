package com.chyzman.chowl.core.graph.node;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import com.kneelawk.graphlib.api.wire.SidedFaceBlockNode;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import static com.chyzman.chowl.core.ChowlCore.id;

public record PanelNode(Direction direction) implements SidedFaceBlockNode {
    public static final Identifier ID = id("panel");
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
