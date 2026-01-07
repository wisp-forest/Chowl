package com.chyzman.chowl.test.client.render;

import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import com.chyzman.chowl.test.util.DefaultedMap;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class FramePanelRenderState extends PartRenderState {
    public Direction face = Direction.NORTH;
    public ItemStack item = ItemStack.EMPTY;
    public NonNullList<ItemStack> upgrades = NonNullList.withSize(8, ItemStack.EMPTY);
    public int count = 0;
    public int size = 64;
    public DefaultedMap<String, ItemStackRenderState> itemRenderStates = new DefaultedMap<>(ItemStackRenderState::new);
    public int lightmapCoordinates;

}
