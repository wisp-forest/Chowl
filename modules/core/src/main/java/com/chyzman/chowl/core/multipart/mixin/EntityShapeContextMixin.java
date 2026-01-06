package com.chyzman.chowl.core.multipart.mixin;

import com.chyzman.chowl.core.pond.ExtendedShapeContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityCollisionContext.class)
public class EntityShapeContextMixin implements ExtendedShapeContext {
    @Shadow @Final private ItemStack heldItem;

    @Override
    public boolean chowl$isHolding(StackPredicate predicate) {
        return predicate.test(heldItem);
    }
}
