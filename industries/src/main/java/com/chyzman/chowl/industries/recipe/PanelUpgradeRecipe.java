package com.chyzman.chowl.industries.recipe;

import com.chyzman.chowl.industries.item.component.CapacityLimitedPanelItem;
import com.chyzman.chowl.industries.item.component.StoragePanelItem;
import com.chyzman.chowl.industries.item.component.UpgradeListComponent;
import com.chyzman.chowl.industries.registry.ChowlComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class PanelUpgradeRecipe extends SpecialCraftingRecipe {
    public final StoragePanelItem item;

    public PanelUpgradeRecipe(CraftingRecipeCategory category, StoragePanelItem item) {
        super(category);
        this.item = item;
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        return getOutput(input) != null;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getOutput(input);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ChowlRecipeSerializers.PANEL_UPGRADE_RECIPE;
    }

    public @Nullable ItemStack getOutput(CraftingRecipeInput inventory) {
        ArrayList<ItemStack> stacks = new ArrayList<>(inventory.getStacks().stream().map(ItemStack::copy).filter(stack -> !stack.isEmpty()).toList());
        if (stacks.size() <= 1) return null;
        for (Item item : stacks.stream().map(ItemStack::getItem).toList()) {
            if (item != this.item) return null;
        }
        while (stacks.size() > 1 && stacks.stream().map(stack -> stack.getItem() == this.item ? CapacityLimitedPanelItem.capacityTier(stack) : BigInteger.ZERO).distinct().count() < stacks.size()) {
            stacks = new ArrayList<>(stacks.stream().sorted(Comparator.comparing(CapacityLimitedPanelItem::capacityTier)).toList());
            for (int i = 0; i < stacks.size() - 1; i++) {
                ItemStack firstStack = stacks.get(i);
                if (firstStack.getItem() == this.item) {
                    ItemStack nextStack = stacks.get(i + 1);
                    if (!item.currentFilter(firstStack).equals(item.currentFilter(nextStack)) &&
                            !item.canSetFilter(firstStack, item.currentFilter(nextStack)) &&
                            !item.canSetFilter(nextStack, item.currentFilter(firstStack))) return null;
                    if (!(CapacityLimitedPanelItem.capacityTier(firstStack).compareTo(CapacityLimitedPanelItem.capacityTier(nextStack)) == 0))
                        return null;

                    List<ItemStack> newUpgrades;

                    if (item.upgrades(firstStack).isEmpty()) {
                        newUpgrades = item.upgrades(nextStack).copyStacks();
                    } else if (item.upgrades(nextStack).isEmpty()) {
                        newUpgrades = item.upgrades(firstStack).copyStacks();
                    } else {
                        newUpgrades = new ArrayList<>();
                        newUpgrades.addAll(item.upgrades(firstStack).upgradeStacks().stream().filter(stack -> !stack.isEmpty()).toList());
                        newUpgrades.addAll(item.upgrades(nextStack).upgradeStacks().stream().filter(stack -> !stack.isEmpty()).toList());
                    }

                    if (newUpgrades.size() > 8) return null;

                    if (item.currentFilter(firstStack).isBlank()) item.setFilter(firstStack, item.currentFilter(nextStack));

                    firstStack.set(ChowlComponents.CAPACITY, CapacityLimitedPanelItem.capacityTier(firstStack).add(BigInteger.ONE));
                    firstStack.set(ChowlComponents.UPGRADE_LIST, new UpgradeListComponent(Collections.unmodifiableList(newUpgrades)));
                    firstStack.set(ChowlComponents.COUNT,
                        firstStack.getOrDefault(ChowlComponents.COUNT, BigInteger.ZERO)
                            .add(nextStack.getOrDefault(ChowlComponents.COUNT, BigInteger.ZERO)));

                    stacks.set(i, firstStack);
                    stacks.remove(nextStack);
                    break;
                }
            }
        }
        return stacks.size() == 1 ? stacks.get(0) : null;
    }
}