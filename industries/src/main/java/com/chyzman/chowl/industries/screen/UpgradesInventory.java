package com.chyzman.chowl.industries.screen;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class UpgradesInventory extends SimpleInventory {
    public UpgradesInventory() {
        super(8);
    }

    public UpgradesInventory(ItemStack... items) {
        super(items);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return super.isValid(slot, stack) && stack.getItem().canBeNested();
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return super.canInsert(stack) && stack.getItem().canBeNested();
    }


}
