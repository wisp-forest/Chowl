package com.chyzman.chowl.transfer;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface PanelStorageContext {
    static PanelStorageContext from(DrawerFrameBlockEntity drawerFrame, Direction side) {
        return new DrawerFrameContext(drawerFrame, side);
    }

    ItemStack stack();
    void setStack(ItemStack stack);

    DrawerFrameBlockEntity drawerFrame();
    Direction frameSide();

    void markDirty();
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
        return drawerFrame.stacks.get(side.getId()).getLeft();
    }

    @Override
    public void setStack(ItemStack stack) {
        var orientation = drawerFrame.stacks.get(side.getId()).getRight();

        drawerFrame.stacks.set(side.getId(), new Pair<>(stack, orientation));
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
