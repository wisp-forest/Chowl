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

public class WitnessedBlastingCriteria extends AbstractCriterion<WitnessedBlastingCriteria.Conditions> {
    public static final Identifier ID = ChowlRegistryHelper.id("witnessed_blasting");

    public void trigger(ServerPlayerEntity entity, boolean nuclear) {
        this.trigger(entity, conditions -> !conditions.requiresNuclear || nuclear);
    }

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public record Conditions(Optional<LootContextPredicate> player, boolean requiresNuclear) implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                    Codec.BOOL.optionalFieldOf("requires_nuclear", false).forGetter(Conditions::requiresNuclear)
                )
                .apply(instance, Conditions::new));
    }
}