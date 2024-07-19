package com.chyzman.chowl.criteria;

import com.chyzman.chowl.Chowl;
import com.chyzman.chowl.event.UpgradeInteractionEvents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class InsertedUpgradeCriteria extends AbstractCriterion<InsertedUpgradeCriteria.Conditions> {
    public static final Identifier ID = Chowl.id("inserted_upgrade");

    public InsertedUpgradeCriteria() {
        UpgradeInteractionEvents.UPGRADE_INSERTED.register((player, frame, side, panel, upgrade) -> {
            this.trigger(player, conditions -> conditions.upgrade.map(x -> x.test(upgrade)).orElse(true));
        });
    }

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public record Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> upgrade) implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                    ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Conditions::upgrade)
                )
                .apply(instance, Conditions::new));
    }
}
