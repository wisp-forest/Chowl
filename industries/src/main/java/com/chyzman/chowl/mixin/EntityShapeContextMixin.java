package com.chyzman.chowl.mixin;

import com.chyzman.chowl.pond.ShapeContextExtended;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityShapeContext.class)
public class EntityShapeContextMixin implements ShapeContextExtended {
    @Shadow @Final private ItemStack heldItem;

    @Override
    public boolean isHolding(StackPredicate predicate) {
        return predicate.test(heldItem);
    }
}
