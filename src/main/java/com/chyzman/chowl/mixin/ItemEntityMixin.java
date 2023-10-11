package com.chyzman.chowl.mixin;

import com.chyzman.chowl.item.component.UpgradeablePanelItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

import static com.chyzman.chowl.Chowl.NETHERITE_UPGRADE_TAG;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow
    public abstract ItemStack getStack();

    @Inject(method = "isFireImmune", at = @At("HEAD"), cancellable = true)
    public void noBurny(CallbackInfoReturnable<Boolean> cir) {
        if (getStack().getItem() instanceof UpgradeablePanelItem panel) {
            if (panel.hasUpgrade(getStack(), stack -> stack.getItem().isFireproof() || stack.isIn(NETHERITE_UPGRADE_TAG))) cir.setReturnValue(true);
        }
    }
}