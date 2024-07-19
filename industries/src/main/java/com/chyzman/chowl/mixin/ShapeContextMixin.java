package com.chyzman.chowl.mixin;

import com.chyzman.chowl.pond.ShapeContextExtended;
import net.minecraft.block.ShapeContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShapeContext.class)
public interface ShapeContextMixin extends ShapeContextExtended {
    @Override
    default boolean isHolding(StackPredicate predicate) {
        return false;
    }
}
