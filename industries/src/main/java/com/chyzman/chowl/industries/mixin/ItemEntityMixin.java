package com.chyzman.chowl.industries.mixin;

import com.chyzman.chowl.industries.item.component.UpgradeablePanelItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.chyzman.chowl.industries.Chowl.BLAST_PROOF_UPGRADE_TAG;
import static com.chyzman.chowl.industries.Chowl.FIREPROOF_UPGRADE_TAG;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow
    public abstract ItemStack getStack();

    @Inject(method = "isFireImmune", at = @At("HEAD"), cancellable = true)
    public void noBurny(CallbackInfoReturnable<Boolean> cir) {
        if (getStack().getItem() instanceof UpgradeablePanelItem panel) {
            if (panel.hasUpgrade(getStack(), stack -> this.getStack().contains(DataComponentTypes.FIRE_RESISTANT) || stack.isIn(FIREPROOF_UPGRADE_TAG))) cir.setReturnValue(true);
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void noBoomy(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.isIn(DamageTypeTags.IS_EXPLOSION) && getStack().getItem() instanceof UpgradeablePanelItem panel) {
            if (panel.hasUpgrade(getStack(), stack -> stack.isIn(BLAST_PROOF_UPGRADE_TAG) || ((stack.getItem() instanceof BlockItem blockItem) && blockItem.getBlock().getBlastResistance() >= 50 && amount >= 15))) cir.setReturnValue(false);
        }
    }
}
