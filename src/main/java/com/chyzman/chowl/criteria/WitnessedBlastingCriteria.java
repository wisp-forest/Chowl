package com.chyzman.chowl.criteria;

import com.chyzman.chowl.util.ChowlRegistryHelper;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class WitnessedBlastingCriteria extends AbstractCriterion<WitnessedBlastingCriteria.Conditions> {
    public static final Identifier ID = ChowlRegistryHelper.id("witnessed_blasting");

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity entity, boolean nuclear) {
        this.trigger(entity, conditions -> !conditions.requiresNuclear || nuclear);
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions(playerPredicate, JsonHelper.getBoolean(obj, "nuclear", false));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final boolean requiresNuclear;

        public Conditions(LootContextPredicate playerPredicate, boolean requiresNuclear) {
            super(ID, playerPredicate);
            this.requiresNuclear = requiresNuclear;
        }
    }
}
