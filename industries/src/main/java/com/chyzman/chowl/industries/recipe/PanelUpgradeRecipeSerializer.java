package com.chyzman.chowl.industries.recipe;

import com.chyzman.chowl.industries.item.component.StoragePanelItem;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.EndecRecipeSerializer;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.item.Item;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;

public class PanelUpgradeRecipeSerializer extends EndecRecipeSerializer<PanelUpgradeRecipe> {
    public static final StructEndec<PanelUpgradeRecipe> ENDEC = StructEndecBuilder.of(
        CodecUtils.toEndec(CraftingRecipeCategory.CODEC).
            optionalFieldOf("category", CraftingRecipe::getCategory, (CraftingRecipeCategory) null),
        MinecraftEndecs.ofRegistry(Registries.ITEM)
            .xmap(PanelUpgradeRecipeSerializer::tryCast, x -> (Item) x)
            .fieldOf("item", x -> x.item),
        PanelUpgradeRecipe::new
    );

    public PanelUpgradeRecipeSerializer() {
        super(ENDEC);
    }

    private static StoragePanelItem tryCast(Item item) {
        if (!(item instanceof StoragePanelItem storage))
            throw new RuntimeException(Registries.ITEM.getId(item) + " isn't a storage panel");

        return storage;
    }
}