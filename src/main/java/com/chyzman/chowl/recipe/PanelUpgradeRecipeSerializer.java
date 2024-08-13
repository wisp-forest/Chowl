package com.chyzman.chowl.recipe;

import com.chyzman.chowl.item.component.StoragePanelItem;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.StructEndec;
import io.wispforest.owo.serialization.endec.BuiltInEndecs;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import io.wispforest.owo.serialization.util.EndecRecipeSerializer;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;

public class PanelUpgradeRecipeSerializer extends EndecRecipeSerializer<PanelUpgradeRecipe> {
    public static final StructEndec<PanelUpgradeRecipe> ENDEC = StructEndecBuilder.of(
        Endec.ofCodec(CraftingRecipeCategory.CODEC).
            optionalFieldOf("category", PanelUpgradeRecipe::getCategory, (CraftingRecipeCategory) null),
        BuiltInEndecs.ofRegistry(Registries.ITEM)
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