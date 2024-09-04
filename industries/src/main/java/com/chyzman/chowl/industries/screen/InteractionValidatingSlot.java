package com.chyzman.chowl.industries.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Optional;
import java.util.function.Predicate;

public class InteractionValidatingSlot extends Slot {
    private final int index;
    private final Predicate<ItemStack> condition;

    public InteractionValidatingSlot(Inventory inventory, int index, int x, int y, Predicate<ItemStack> condition) {
        super(inventory, index, x, y);
        this.index = index;
        this.condition = condition;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return super.canInsert(stack) && condition.test(stack);
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return super.canTakeItems(playerEntity) && condition.test(this.getStack());
    }

    @Override
    public boolean canTakePartial(PlayerEntity player) {
        return super.canTakePartial(player) && condition.test(this.getStack());
    }

    @Override
    public boolean canBeHighlighted() {
        return super.canBeHighlighted() && condition.test(this.getStack());
    }

    @Override
    public void onQuickTransfer(ItemStack newItem, ItemStack original) {
        if (condition.test(newItem)) {
            super.onQuickTransfer(newItem, original);
        }
    }
}
