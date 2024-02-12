package com.chyzman.chowl.criteria;

import com.chyzman.chowl.event.UpgradeInteractionEvents;
import com.chyzman.chowl.util.ChowlRegistryHelper;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class InsertedUpgradeCriteria extends AbstractCriterion<InsertedUpgradeCriteria.Conditions> {
    public static final Identifier ID = ChowlRegistryHelper.id("inserted_upgrade");

    @Override
    public Identifier getId() {
        return ID;
    }

    public InsertedUpgradeCriteria() {
        UpgradeInteractionEvents.UPGRADE_INSERTED.register((player, frame, side, panel, upgrade) -> {
            this.trigger(player, conditions -> conditions.upgrade.test(upgrade));
        });
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        var upgrade = obj.has("item") ? ItemPredicate.fromJson(obj.get("item")) : ItemPredicate.ANY;

        return new Conditions(playerPredicate, upgrade);
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final ItemPredicate upgrade;

        public Conditions(LootContextPredicate playerPredicate, ItemPredicate upgrade) {
            super(ID, playerPredicate);
            this.upgrade = upgrade;
        }
    }
}
