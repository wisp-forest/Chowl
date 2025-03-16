package com.chyzman.chowl.core.graph.node;

import com.chyzman.chowl.core.Chowl;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import com.kneelawk.graphlib.api.wire.FullWireBlockNode;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class PanelHolderNode implements FullWireBlockNode {
    public static final Identifier ID = Chowl.id("panel_holder");
    public static final PanelHolderNode INSTANCE = new PanelHolderNode();
    public static final BlockNodeType TYPE = BlockNodeType.of(ID, () -> INSTANCE);

    @Override
    public @NotNull BlockNodeType getType() {
        return TYPE;
    }

    @Override
    public void onConnectionsChanged(@NotNull NodeHolder<BlockNode> nodeHolder) {

    }
}
