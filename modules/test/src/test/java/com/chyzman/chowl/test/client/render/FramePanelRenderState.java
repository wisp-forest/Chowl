package com.chyzman.chowl.test.client.render;

import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

public class FramePanelRenderState extends PartRenderState {
    public Direction face = Direction.NORTH;
    public ItemStack item = ItemStack.EMPTY;
    public DefaultedList<ItemStack> upgrades = DefaultedList.ofSize(8, ItemStack.EMPTY);
    public int count = 0;
    public int size = 64;
    public ItemRenderState removeButtonRenderState = new ItemRenderState();
}
