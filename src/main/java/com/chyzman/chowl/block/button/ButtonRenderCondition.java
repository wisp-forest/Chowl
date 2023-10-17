package com.chyzman.chowl.block.button;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.hit.BlockHitResult;

public interface ButtonRenderCondition {
    ButtonRenderCondition ALWAYS = (blockEntity, hitResult, blockTargeted, panelTargeted, buttonTargeted) -> true;
    ButtonRenderCondition BLOCK_FOCUSED = (blockEntity, hitResult, blockTargeted, panelTargeted, buttonTargeted) -> blockTargeted;
    ButtonRenderCondition PANEL_FOCUSED = (blockEntity, hitResult, blockTargeted, panelTargeted, buttonTargeted) -> panelTargeted;
    ButtonRenderCondition BUTTON_FOCUSED = (blockEntity, hitResult, blockTargeted, panelTargeted, buttonTargeted) -> buttonTargeted;
    ButtonRenderCondition NEVER = (blockEntity, hitResult, blockTargeted, panelTargeted, buttonTargeted) -> false;

    boolean shouldRender(BlockEntity blockEntity, BlockHitResult hitResult, boolean blockTargeted, boolean panelTargeted, boolean buttonTargeted);
}
