package com.chyzman.chowl.industries.item.component;

import com.chyzman.chowl.industries.util.ChowlEndecs;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Unmodifiable;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record BareItemsComponent(@Unmodifiable Map<Item, BigInteger> entries) {
    public static final Endec<BareItemsComponent> ENDEC = StructEndecBuilder.of(
        Endec.map(item -> Registries.ITEM.getId(item).toString(), str -> Registries.ITEM.get(Identifier.of(str)), ChowlEndecs.BIG_INTEGER)
            .fieldOf("entries", BareItemsComponent::entries),
        BareItemsComponent::new
    );

    public static final BareItemsComponent DEFAULT = new BareItemsComponent(Collections.emptyMap());

    public BigInteger totalCount() {
        BigInteger counter = BigInteger.ZERO;
        
        for (BigInteger count : entries.values()) {
            counter = counter.add(count);
        }
        
        return counter;
    }
    
    public BareItemsComponent copyAndInsert(Item item, BigInteger count) {
        var newMap = new HashMap<>(entries);
        newMap.merge(item, count, BigInteger::add);
        return new BareItemsComponent(Collections.unmodifiableMap(newMap));
    }

    public BareItemsComponent copyAndSet(Item item, BigInteger count) {
        var newMap = new HashMap<>(entries);

        if (count.signum() < 1) {
            newMap.remove(item);
        } else {
            newMap.put(item, count);
        }

        return new BareItemsComponent(Collections.unmodifiableMap(newMap));
    }
}
