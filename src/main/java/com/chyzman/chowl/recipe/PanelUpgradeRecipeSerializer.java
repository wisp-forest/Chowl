package com.chyzman.chowl.recipe;

import com.chyzman.chowl.item.component.CapacityLimitedPanelItem;
import com.chyzman.chowl.item.component.FilteringPanelItem;
import com.chyzman.chowl.item.component.UpgradeablePanelItem;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class PanelUpgradeRecipeSerializer implements RecipeSerializer<PanelUpgradeRecipe<?>> {

    @Override
    public PanelUpgradeRecipe<?> read(Identifier id, JsonObject json) {
        var item = JsonHelper.getItem(json, "item");
        return read(id, item, JsonHelper.getString(json, "category", null));
    }

    @Override
    public PanelUpgradeRecipe<?> read(Identifier id, PacketByteBuf buf) {
        var category = buf.readString();
        var item = buf.readRegistryValue(Registries.ITEM);
        return read(id, item, category);
    }

    public PanelUpgradeRecipe<?> read(Identifier id, Item item, String category) {
        CraftingRecipeCategory craftingRecipeCategory = CraftingRecipeCategory.CODEC.byId(category, CraftingRecipeCategory.MISC);
        return new PanelUpgradeRecipe<>(id, craftingRecipeCategory, tryCast(item, id));
    }

    @Override
    public void write(PacketByteBuf buf, PanelUpgradeRecipe recipe) {
        buf.writeString(recipe.getCategory().toString());
        buf.writeRegistryValue(Registries.ITEM, recipe.item);
    }

    @SuppressWarnings("unchecked")
    public <T extends Item & CapacityLimitedPanelItem & FilteringPanelItem & UpgradeablePanelItem> T tryCast(Item item, Identifier id) {
        StringBuilder projectileFront = new StringBuilder(item.getName().getString()).append("isn't ");
        String projectileEnd = ". Recipe: \"" + id + "\"";
        if (!(item instanceof CapacityLimitedPanelItem))
            throw new RuntimeException(projectileFront.append("Capacity Limited").append(projectileEnd).toString());
        if (!(item instanceof FilteringPanelItem))
            throw new RuntimeException(projectileFront.append("Filtering").append(projectileEnd).toString());
        if (!(item instanceof UpgradeablePanelItem))
            throw new RuntimeException(projectileFront.append("Upgradeable").append(projectileEnd).toString());
        return (T) item;
    }
}