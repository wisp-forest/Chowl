package com.chyzman.chowl.criteria;

import com.chyzman.chowl.util.ChowlRegistryHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class LabeledPanelCriteria extends AbstractCriterion<LabeledPanelCriteria.Conditions> {
    public static final Identifier ID = ChowlRegistryHelper.id("labeled_panel");

    public void trigger(ServerPlayerEntity entity, String name) {
        this.trigger(entity, conditions -> conditions.requiredName.map(name::equals).orElse(true));
    }

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public record Conditions(Optional<LootContextPredicate> player, Optional<String> requiredName) implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                    Codec.STRING.optionalFieldOf("required_name").forGetter(Conditions::requiredName)
                )
                .apply(instance, Conditions::new));
    }
}
