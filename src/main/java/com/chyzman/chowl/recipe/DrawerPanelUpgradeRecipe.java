package com.chyzman.chowl.recipe;

import com.chyzman.chowl.item.DrawerPanelItem;
import com.chyzman.chowl.item.component.CapacityLimitedPanelItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;

public class DrawerPanelUpgradeRecipe extends SpecialCraftingRecipe {
    public DrawerPanelUpgradeRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        return getOutput(inventory) != null;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        return getOutput(inventory);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ChowlRecipeSerializers.DRAWER_PANEL_UPGRADE_RECIPE;
    }

    public @Nullable ItemStack getOutput(RecipeInputInventory inventory) {
        Item outputType = null;
        ArrayList<ItemStack> stacks = new ArrayList<>(inventory.getInputStacks().stream().map(ItemStack::copy).filter(stack -> !stack.isEmpty()).toList());
        if (stacks.size() <= 1) return null;
        for (Item item : stacks.stream().map(ItemStack::getItem).toList()) {
            if (!(item instanceof DrawerPanelItem)) return null;

            if (!item.equals(outputType) && outputType != null) {
                return null;
            } else {
                outputType = item;
            }
        }
        while (stacks.size() > 1 && stacks.stream().map(stack -> stack.getItem() instanceof DrawerPanelItem ? CapacityLimitedPanelItem.capacityTier(stack) : BigInteger.ZERO).distinct().count() < stacks.size()) {
            stacks = new ArrayList<>(stacks.stream().sorted(Comparator.comparing(CapacityLimitedPanelItem::capacityTier)).toList());
            for (int i = 0; i < stacks.size() - 1; i++) {
                if (stacks.get(i).getItem() instanceof DrawerPanelItem panel) {
                    if (!panel.displayedVariant(stacks.get(i)).equals(panel.displayedVariant(stacks.get(i + 1)))) return null;
                    if (!(CapacityLimitedPanelItem.capacityTier(stacks.get(i)).compareTo(CapacityLimitedPanelItem.capacityTier(stacks.get(i + 1))) == 0)) return null;
                    ArrayList<ItemStack> newUpgrades = new ArrayList<>();
                    if (panel.upgrades(stacks.get(i)).stream().filter(stack -> !stack.isEmpty()).toList().isEmpty()) {
                        newUpgrades = new ArrayList<>(panel.upgrades(stacks.get(i + 1)));
                    } else if (panel.upgrades(stacks.get(i + 1)).stream().filter(stack -> !stack.isEmpty()).toList().isEmpty()) {
                        newUpgrades = new ArrayList<>(panel.upgrades(stacks.get(i)));
                    } else {
                        newUpgrades.addAll(panel.upgrades(stacks.get(i)).stream().filter(stack -> !stack.isEmpty()).toList());
                        newUpgrades.addAll(panel.upgrades(stacks.get(i + 1)).stream().filter(stack -> !stack.isEmpty()).toList());
                    }
                    if (newUpgrades.size() > 8) return null;
                    var newStack = stacks.get(i);
                    newStack.put(CapacityLimitedPanelItem.CAPACITY, CapacityLimitedPanelItem.capacityTier(stacks.get(i)).add(BigInteger.ONE));
                    panel.setUpgrades(newStack, newUpgrades);
                    newStack.put(DrawerPanelItem.COUNT, stacks.get(i).get(DrawerPanelItem.COUNT).add(stacks.get(i + 1).get(DrawerPanelItem.COUNT)));
                    stacks.set(i, newStack);
                    stacks.remove(i + 1);
                    break;
                }
            }
        }
        return stacks.size() == 1 ? stacks.get(0) : null;
    }
}