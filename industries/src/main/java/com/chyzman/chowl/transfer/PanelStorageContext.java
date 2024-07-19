package com.chyzman.chowl.transfer;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.client.RenderGlobals;
import com.chyzman.chowl.graph.GraphStore;
import com.chyzman.chowl.item.component.PanelItem;
import com.chyzman.chowl.registry.ChowlBlocks;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public interface PanelStorageContext {
    static PanelStorageContext from(DrawerFrameBlockEntity drawerFrame, Direction side) {
        return new DrawerFrameContext(drawerFrame, side);
    }

    static PanelStorageContext forRendering(ItemStack stack) {
        return new RenderPanelContext(stack);
    }

    ItemStack stack();
    void setStack(ItemStack stack);

    DrawerFrameBlockEntity drawerFrame();
    Direction frameSide();

    void markDirty();

    default boolean traverseNetwork(Consumer<SlottedStorage<ItemVariant>> consumer) {
        if (drawerFrame() == null) return false;

        World w = drawerFrame().getWorld();

        if (w == null) return false;

        GraphStore store = GraphStore.get(w);
        var graph = store.getGraphFor(drawerFrame().getPos());

        if (graph == null) return false;

        for (var node : graph.nodes()) {
            if (!node.state().isOf(ChowlBlocks.DRAWER_FRAME)) continue;

            var otherBE = w.getBlockEntity(node.pos());
            if (!(otherBE instanceof DrawerFrameBlockEntity otherFrame)) continue;

            for (int sideId = 0; sideId < 6; sideId++) {
                var ctx = PanelStorageContext.from(otherFrame, Direction.byId(sideId));
                if (!(ctx.stack().getItem() instanceof PanelItem panelItem)) continue;

                var storage = panelItem.getNetworkStorage(ctx);

                if (storage != null) consumer.accept(storage);
            }
        }

        return true;
    }
}

class DrawerFrameContext implements PanelStorageContext {
    private final DrawerFrameBlockEntity drawerFrame;
    private final Direction side;

    public DrawerFrameContext(DrawerFrameBlockEntity drawerFrame, Direction side) {
        this.drawerFrame = drawerFrame;
        this.side = side;
    }

    @Override
    public ItemStack stack() {
        return drawerFrame.stacks.get(side.getId()).stack();
    }

    @Override
    public void setStack(ItemStack stack) {
        var old = drawerFrame.stacks.get(side.getId());

        drawerFrame.stacks.set(side.getId(), old.withStack(stack));
    }

    @Override
    public @Nullable DrawerFrameBlockEntity drawerFrame() {
        return drawerFrame;
    }

    @Override
    public @Nullable Direction frameSide() {
        return side;
    }

    @Override
    public void markDirty() {
        drawerFrame.markDirty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrawerFrameContext that = (DrawerFrameContext) o;

        if (!drawerFrame.equals(that.drawerFrame)) return false;
        return side == that.side;
    }

    @Override
    public int hashCode() {
        int result = drawerFrame.hashCode();
        result = 31 * result + side.hashCode();
        return result;
    }
}

class RenderPanelContext implements PanelStorageContext {
    private final ItemStack stack;
    private final @Nullable DrawerFrameBlockEntity drawerFrame;
    private final @Nullable Direction side;

    public RenderPanelContext(ItemStack stack) {
        this.stack = stack;
        this.drawerFrame = RenderGlobals.DRAWER_FRAME.get();
        this.side = RenderGlobals.FRAME_SIDE.get();
    }

    @Override
    public ItemStack stack() {
        return stack;
    }

    @Override
    public void setStack(ItemStack stack) {
        throw new IllegalStateException("Cannot modify panel during rendering");
    }

    @Override
    public DrawerFrameBlockEntity drawerFrame() {
        return drawerFrame;
    }

    @Override
    public Direction frameSide() {
        return side;
    }

    @Override
    public void markDirty() {
        throw new IllegalStateException("Cannot modify panel during rendering");
    }
}