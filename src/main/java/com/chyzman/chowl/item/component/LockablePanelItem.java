package com.chyzman.chowl.item.component;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.block.button.BlockButton;
import com.chyzman.chowl.block.button.ButtonRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public interface LockablePanelItem extends FilteringPanelItem {
    BlockButton LOCK_BUTTON = BlockButton.builder(0, 14, 2, 16)
            .renderWhen((blockEntity, side, blockTargeted, panelTargeted, buttonTargeted) -> {
                var panel = ((DrawerFrameBlockEntity) blockEntity).stacks.get(side.getId()).stack;
                return panel.getItem() instanceof LockablePanelItem lockable && lockable.locked(panel);
            })
            .renderer(ButtonRenderer.stack(Items.POTATO.getDefaultStack()))
            .build();

    boolean locked(ItemStack stack);

    void setLocked(ItemStack stack, boolean locked);
}