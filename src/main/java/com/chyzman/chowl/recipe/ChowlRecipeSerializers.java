package com.chyzman.chowl.recipe;

import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

public class ChowlRecipeSerializers {
    public static final PanelUpgradeRecipeSerializer PANEL_UPGRADE_RECIPE = new PanelUpgradeRecipeSerializer();

    public static void init() {
        Registry.register(Registries.RECIPE_SERIALIZER, id("panel_upgrade"), PANEL_UPGRADE_RECIPE);
    }

}