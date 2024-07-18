package com.chyzman.chowl.item.component;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.block.button.BlockButton;
import com.chyzman.chowl.block.button.ButtonRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

public interface LockablePanelItem extends FilteringPanelItem {
    BlockButton LOCK_BUTTON = PanelItem.buttonBuilder(0, 14, 2, 16)
            .onUse((world, frame, useSide, stack, player) -> {
                if (!(stack.getItem() instanceof LockablePanelItem lockable)) return ActionResult.PASS;
                lockable.setLocked(stack, !lockable.locked(stack));
                frame.stacks.set(useSide.getId(), frame.stacks.get(useSide.getId()).withStack(stack));
                frame.markDirty();
                return ActionResult.SUCCESS;
            })
            .renderWhen((blockEntity, side, blockTargeted, panelTargeted, buttonTargeted) -> {
                var panel = ((DrawerFrameBlockEntity) blockEntity).stacks.get(side.getId()).stack();
                return panel.getItem() instanceof LockablePanelItem lockable && lockable.locked(panel);
            })
            .renderer(ButtonRenderer.model(id("item/lock")))
//            .renderer(ButtonRenderer.stack(Items.POTATO.getDefaultStack()))
            .build();

    boolean locked(ItemStack stack);

    void setLocked(ItemStack stack, boolean locked);
}