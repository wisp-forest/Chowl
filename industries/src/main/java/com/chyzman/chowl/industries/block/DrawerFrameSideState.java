package com.chyzman.chowl.industries.block;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.item.ItemStack;

public record DrawerFrameSideState(ItemStack stack, int orientation, boolean isBlank) {
    public static final Endec<DrawerFrameSideState> ENDEC = StructEndecBuilder.of(
        MinecraftEndecs.ITEM_STACK.fieldOf("Stack", DrawerFrameSideState::stack),
        Endec.INT.fieldOf("Orientation", DrawerFrameSideState::orientation),
        Endec.BOOLEAN.fieldOf("IsBlank", DrawerFrameSideState::isBlank),
        DrawerFrameSideState::new
    );

    public static DrawerFrameSideState empty() {
        return new DrawerFrameSideState(ItemStack.EMPTY, 0, false);
    }

    public DrawerFrameSideState withStack(ItemStack stack) {
        return new DrawerFrameSideState(stack, orientation, isBlank);
    }

    public boolean isEmpty() {
        return stack.isEmpty() && !isBlank;
    }
}
