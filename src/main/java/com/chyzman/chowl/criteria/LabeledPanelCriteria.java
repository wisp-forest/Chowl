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

public class LabeledPanelCriteria extends AbstractCriterion<LabeledPanelCriteria.Conditions> {
    public static final Identifier ID = ChowlRegistryHelper.id("labeled_panel");

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity entity, String name) {
        this.trigger(entity, conditions -> conditions.requiredName == null || conditions.requiredName.equals(name));
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions(playerPredicate, JsonHelper.getString(obj, "name", null));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final String requiredName;

        public Conditions(LootContextPredicate playerPredicate, String requiredName) {
            super(ID, playerPredicate);
            this.requiredName = requiredName;
        }
    }
}
