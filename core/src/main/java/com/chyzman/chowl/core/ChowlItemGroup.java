package com.chyzman.chowl.core;

import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.util.OwoFreezer;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ChowlItemGroup {
    private static Supplier<Icon> CURRENT_ICON = () -> Icon.of(Items.SPONGE);
    private static int ICON_PRIORITY = -1;

    private static final TreeSet<Pair<Consumer<OwoItemGroup>, Integer>> INITIALIZERS = new TreeSet<>(Comparator.comparing(Pair::getRight));

    private static final OwoItemGroup GROUP = OwoItemGroup.builder(ChowlCore.id("group"), ChowlItemGroup::getIcon)
        .initializer(group -> {
            for (var entry : INITIALIZERS) {
                entry.getLeft().accept(group);
            }
        })
        .build();

    static {
        OwoFreezer.checkRegister("The Chowl item group");
        OwoFreezer.registerFreezeCallback(GROUP::initialize);
    }

    public static void proposeIcon(Supplier<Icon> iconSupplier, int priority) {
        OwoFreezer.checkRegister("Chowl item group icons");

        if (priority > ICON_PRIORITY) {
            CURRENT_ICON = iconSupplier;
            ICON_PRIORITY = priority;
        }
    }

    public static void addInitializer(Consumer<OwoItemGroup> initializer, int priority) {
        OwoFreezer.checkRegister("Chowl item group initializers");

        INITIALIZERS.add(new Pair<>(initializer, priority));
    }

    private static Icon getIcon() {
        return CURRENT_ICON.get();
    }
}
