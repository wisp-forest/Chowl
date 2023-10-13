package com.chyzman.chowl.recipe;

import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

public class ChowlRecipeSerializers {
    public static final SpecialRecipeSerializer<DrawerPanelUpgradeRecipe> DRAWER_PANEL_UPGRADE_RECIPE = new SpecialRecipeSerializer<>(DrawerPanelUpgradeRecipe::new);

    public static void init() {
        Registry.register(Registries.RECIPE_SERIALIZER, id("drawer_panel_upgrade"), DRAWER_PANEL_UPGRADE_RECIPE);
    }

}