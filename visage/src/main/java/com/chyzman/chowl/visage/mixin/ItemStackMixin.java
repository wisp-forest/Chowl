package com.chyzman.chowl.visage.mixin;


import com.chyzman.chowl.visage.block.VisageBlockTemplate;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "areItemsAndComponentsEqual", at = @At("HEAD"), cancellable = true)
    private static void makeVisagesStack$areItemsAndComponentsEqual(ItemStack left, ItemStack right, CallbackInfoReturnable<Boolean> cir) {
        if (forceVisageStacking(left, right)) cir.setReturnValue(true);
    }

    private static boolean forceVisageStacking(ItemStack left, ItemStack right) {
        return left.getItem() instanceof BlockItem leftBlockItem &&
                right.getItem() instanceof BlockItem rightBlockItem &&
                leftBlockItem.getBlock() instanceof VisageBlockTemplate &&
                rightBlockItem.getBlock() instanceof VisageBlockTemplate &&
                leftBlockItem.getBlock() == rightBlockItem.getBlock();
    }
}
