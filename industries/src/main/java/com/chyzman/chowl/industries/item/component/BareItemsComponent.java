package com.chyzman.chowl.industries.item.component;

import com.chyzman.chowl.industries.util.ChowlEndecs;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Unmodifiable;

import java.math.BigInteger;
import java.util.*;

public record BareItemsComponent(@Unmodifiable Map<Item, BigInteger> entries) {
    private static final Endec<Pair<Item, BigInteger>> ENTRY_ENDEC = StructEndecBuilder.of(
        MinecraftEndecs.ofRegistry(Registries.ITEM).fieldOf("id", Pair::getLeft),
        ChowlEndecs.BIG_INTEGER.fieldOf("count", Pair::getRight),
        Pair::new
    );

    public static final Endec<BareItemsComponent> ENDEC = StructEndecBuilder.of(
        ENTRY_ENDEC
            .listOf()
            .xmap(list -> {
                Map<Item, BigInteger> map = new LinkedHashMap<>(list.size());

                for (var entry : list) map.put(entry.getLeft(), entry.getRight());

                return map;
            }, map -> {
                var list = new ArrayList<Pair<Item, BigInteger>>();

                for (var entry : map.entrySet()) list.add(new Pair<>(entry.getKey(), entry.getValue()));

                return list;
            })
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
        var newMap = new LinkedHashMap<>(entries);
        newMap.merge(item, count, BigInteger::add);
        return new BareItemsComponent(Collections.unmodifiableMap(newMap));
    }

    public BareItemsComponent copyAndSet(Item item, BigInteger count) {
        var newMap = new LinkedHashMap<>(entries);

        if (count.signum() < 1) {
            newMap.remove(item);
        } else {
            newMap.put(item, count);
        }

        return new BareItemsComponent(Collections.unmodifiableMap(newMap));
    }
}
