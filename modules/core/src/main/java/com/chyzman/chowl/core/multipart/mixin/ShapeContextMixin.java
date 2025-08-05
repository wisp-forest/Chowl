package com.chyzman.chowl.core.multipart.mixin;

import com.chyzman.chowl.core.pond.ExtendedShapeContext;
import net.minecraft.block.ShapeContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShapeContext.class)
public interface ShapeContextMixin extends ExtendedShapeContext {
    @Override
    default boolean chowl$isHolding(StackPredicate predicate) {
        return false;
    }
}
