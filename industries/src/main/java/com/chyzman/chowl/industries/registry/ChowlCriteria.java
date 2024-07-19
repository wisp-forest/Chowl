package com.chyzman.chowl.industries.registry;

import com.chyzman.chowl.industries.criteria.InsertedUpgradeCriteria;
import com.chyzman.chowl.industries.criteria.LabeledPanelCriteria;
import com.chyzman.chowl.industries.criteria.WitnessedBlastingCriteria;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ChowlCriteria implements AutoRegistryContainer<Criterion<?>> {
    public static final WitnessedBlastingCriteria WITNESSED_BLASTING = new WitnessedBlastingCriteria();
    public static final LabeledPanelCriteria LABELED_PANEL = new LabeledPanelCriteria();
    public static final InsertedUpgradeCriteria INSERTED_UPGRADE = new InsertedUpgradeCriteria();

    @Override
    public Registry<Criterion<?>> getRegistry() {
        return Registries.CRITERION;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Criterion<?>> getTargetFieldType() {
        return (Class<Criterion<?>>)(Object) Criterion.class;
    }
}
