package com.chyzman.chowl.item.component;

import com.chyzman.chowl.block.BlockButtonProvider;
import com.chyzman.chowl.item.renderer.button.ButtonRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import static com.chyzman.chowl.block.DrawerFrameBlock.getSide;

public interface LockablePanelItem extends PanelItem {
    BlockButtonProvider.Button LOCK_BUTTON = new BlockButtonProvider.ButtonBuilder(0, 14, 2, 16)
            .onRender((entity, hitResult, blockTargeted, panelTargeted, buttonTargeted) -> {
                var side = getSide(hitResult);
                var panel = entity.stacks.get(side.getId()).getLeft();
                if (panel.getItem() instanceof LockablePanelItem lockable && lockable.locked(panel)) {
                    var stack = Items.POTATO.getDefaultStack();
                    return new ButtonRenderer.StackButtonRenderer(stack);
                }
                return null;
            })
            .build();

    boolean locked(ItemStack stack);

    void setLocked(ItemStack stack, boolean locked);
}