package com.chyzman.chowl.core.block.api;

import net.minecraft.block.BlockState;

import java.util.ArrayList;
import java.util.List;

public class FluidFillHandler {
    private static final List<CanFill> PREDICATES = new ArrayList<>();

    public static void canNotFill(CanFill predicate) {
        PREDICATES.add(predicate);
    }

    public static List<CanFill> getPredicates() {
        return PREDICATES;
    }

    public interface CanFill {
        boolean canNotFill(BlockState state);
    }
}
