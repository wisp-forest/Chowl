package com.chyzman.chowl.item.component;

import com.chyzman.chowl.block.BlockButtonProvider;
import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;

import static com.chyzman.chowl.block.DrawerFrameBlock.getSide;

public interface LockablePanelItem extends PanelItem {
    BlockButtonProvider.Button LOCK_BUTTON = new BlockButtonProvider.Button(0, 14, 2, 16,
            null,
            null,
            null,
            (client, entity, hitResult, vertexConsumers, matrices, light, overlay, hovered) -> {
                var side = getSide(hitResult);
                var panel = entity.stacks.get(side.getId()).getLeft();
                if (panel.getItem() instanceof LockablePanelItem lockable && lockable.locked(panel)) {
                    var stack = Items.POTATO.getDefaultStack();
                    client.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, overlay, client.getItemRenderer().getModels().getModel(stack));
                }
            });

    boolean locked(ItemStack stack);

    void setLocked(ItemStack stack, boolean locked);
}