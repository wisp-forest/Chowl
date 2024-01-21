package com.chyzman.chowl.pond;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ShapeContextExtended {
    boolean isHolding(StackPredicate predicate);

    // Intentionally a separate interface to curb any worries about signature collisions
    interface StackPredicate {
        boolean test(ItemStack stack);
    }
}
