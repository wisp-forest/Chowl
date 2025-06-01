package com.chyzman.chowl.core.multipart.mixin;

import com.chyzman.chowl.core.pond.ShapeContextExtended;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityShapeContext.class)
public class EntityShapeContextMixin implements ShapeContextExtended {
    @Shadow @Final private ItemStack heldItem;

    @Override
    public boolean chowl$isHolding(StackPredicate predicate) {
        return predicate.test(heldItem);
    }
}