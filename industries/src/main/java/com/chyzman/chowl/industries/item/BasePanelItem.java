package com.chyzman.chowl.industries.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.Optional;

public abstract class BasePanelItem extends Item {
    public BasePanelItem(Settings settings) {
        super(settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Optional<net.minecraft.item.tooltip.TooltipData> getTooltipData(ItemStack stack) {
        return Optional.of(new TooltipData(stack));
    }

    public record TooltipData(ItemStack stack) implements net.minecraft.item.tooltip.TooltipData {
    }

    @Override
    public boolean canBeNested() {
        return false;
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }
}
