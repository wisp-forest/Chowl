package com.chyzman.chowl.core.graph.node;

import com.google.common.collect.ImmutableMap;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import com.kneelawk.graphlib.api.wire.SidedFaceBlockNode;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.chyzman.chowl.core.ChowlCore.id;

public class PanelNode implements SidedFaceBlockNode {
    public static final Identifier ID = id("panel");
    public static final Map<Direction, PanelNode> INSTANCES = Util.<ImmutableMap.Builder<Direction, PanelNode>>make(ImmutableMap.builder(), (builder) -> {
        for (var dir : Direction.values()) {
            builder.put(dir, new PanelNode(dir));
        }
    }).build();
    public static final BlockNodeType TYPE = BlockNodeType.of(ID, Direction.CODEC.xmap(INSTANCES::get, PanelNode::getSide));
    private final Direction direction;

    private PanelNode(Direction direction) {
        this.direction = direction;
    }

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
