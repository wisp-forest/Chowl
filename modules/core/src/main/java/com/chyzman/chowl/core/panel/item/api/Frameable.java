package com.chyzman.chowl.core.panel.item.api;

import com.chyzman.chowl.core.multipart.api.Part;
import net.minecraft.world.item.ItemStack;

public interface Frameable {
    void addSubParts(Part part, ItemStack stack);
}
