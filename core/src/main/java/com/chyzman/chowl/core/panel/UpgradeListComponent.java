package com.chyzman.chowl.core.panel;

import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public record UpgradeListComponent(@Unmodifiable List<ItemStack> upgradeStacks) {
    public static final Endec<UpgradeListComponent> ENDEC = MinecraftEndecs.ITEM_STACK
        .listOf()
        .xmap(UpgradeListComponent::new, UpgradeListComponent::upgradeStacks);

    public static final UpgradeListComponent DEFAULT = new UpgradeListComponent(List.of(
        ItemStack.EMPTY,
        ItemStack.EMPTY,
        ItemStack.EMPTY,
        ItemStack.EMPTY,
        ItemStack.EMPTY,
        ItemStack.EMPTY,
        ItemStack.EMPTY,
        ItemStack.EMPTY
    ));

    public ItemStack get(int index) {
        return upgradeStacks.get(index);
    }

    public @Nullable ItemStack findUpgrade(Predicate<ItemStack> predicate) {
        for (ItemStack stack : upgradeStacks) {
            if (predicate.test(stack)) return stack;
        }

        return null;
    }

    public boolean isEmpty() {
        for (ItemStack stack : upgradeStacks) {
            if (!stack.isEmpty()) return false;
        }

        return true;
    }

    public UpgradeListComponent set(int index, ItemStack newStack) {
        List<ItemStack> newList = new ArrayList<>(upgradeStacks);
        newList.set(index, newStack);
        return new UpgradeListComponent(Collections.unmodifiableList(newList));
    }

    public List<ItemStack> copyStacks() {
        List<ItemStack> newList = new ArrayList<>(upgradeStacks.size());

        for (var stack : upgradeStacks) {
            newList.add(stack.copy());
        }

        return newList;
    }
}
