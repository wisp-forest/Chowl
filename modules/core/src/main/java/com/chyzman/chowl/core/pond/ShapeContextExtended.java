package com.chyzman.chowl.core.pond;

import net.minecraft.item.ItemStack;

public interface ShapeContextExtended {
    boolean chowl$isHolding(StackPredicate predicate);

    // Intentionally a separate interface to curb any worries about signature collisions
    interface StackPredicate {
        boolean test(ItemStack stack);
    }
}