package com.chyzman.chowl.core.graph.node;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import com.kneelawk.graphlib.api.wire.FullWireBlockNode;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static com.chyzman.chowl.core.ChowlCore.id;

public class PanelHolderNode implements FullWireBlockNode {
    public static final Identifier ID = id("panel_holder");
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
