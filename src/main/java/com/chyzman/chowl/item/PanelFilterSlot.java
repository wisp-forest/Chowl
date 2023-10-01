package com.chyzman.chowl.item;

import io.wispforest.owo.client.screens.ValidatingSlot;
import io.wispforest.owo.ops.ItemOps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class PanelFilterSlot extends DoubleValitatingSlot {
    private final int index;

    public PanelFilterSlot(Inventory inventory, int index, int x, int y, Predicate<ItemStack> insertCondition, Predicate<ItemStack> extractCondition) {
        super(inventory, index, x, y, insertCondition, extractCondition);
        this.index = index;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return super.canInsert(stack) && !(stack.getItem() instanceof PanelItem);
    }

    @Override
    public ItemStack takeStack(int amount) {
        this.inventory.setStack(index, ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertStack(ItemStack stack, int count) {
        if (this.canInsert(stack)) {
            var temp = ItemOps.singleCopy(stack);
            this.inventory.setStack(index, temp);
        }
        return stack;
    }
}