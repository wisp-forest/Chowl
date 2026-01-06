package com.chyzman.chowl.core.multipart.mixin;

import com.chyzman.chowl.core.pond.ExtendedShapeContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CollisionContext.class)
public interface ShapeContextMixin extends ExtendedShapeContext {
    @Override
    default boolean chowl$isHolding(StackPredicate predicate) {
        return false;
    }
}
