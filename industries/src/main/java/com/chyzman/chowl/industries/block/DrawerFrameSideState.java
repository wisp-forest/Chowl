package com.chyzman.chowl.industries.block;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record DrawerFrameSideState(ItemStack stack, int orientation, boolean isBlank) {
    public static final Endec<DrawerFrameSideState> ENDEC = StructEndecBuilder.of(
        MinecraftEndecs.ITEM_STACK.fieldOf("Stack", DrawerFrameSideState::stack),
        Endec.INT.fieldOf("Orientation", DrawerFrameSideState::orientation),
        Endec.BOOLEAN.fieldOf("IsBlank", DrawerFrameSideState::isBlank),
        DrawerFrameSideState::new
    );

    public static final Endec<List<DrawerFrameSideState>> LIST_ENDEC = ENDEC.listOf()
        .validate(list -> {
            if (list.size() != 6) throw new IllegalStateException("list of DrawerFrameSideStates must have 6 entries");
        });

    public static DrawerFrameSideState empty() {
        return new DrawerFrameSideState(ItemStack.EMPTY, 0, false);
    }

    public DrawerFrameSideState withStack(ItemStack stack) {
        return new DrawerFrameSideState(stack, orientation, isBlank);
    }

    public boolean isEmpty() {
        return stack.isEmpty() && !isBlank;
    }
    
    public static List<DrawerFrameSideState> copyList(List<DrawerFrameSideState> original) {
        List<DrawerFrameSideState> newList = new ArrayList<>(6);
        
        for (DrawerFrameSideState side : original) {
            newList.add(new DrawerFrameSideState(side.stack().copy(), side.orientation(), side.isBlank()));
        }
        
        return newList;
    }
}
