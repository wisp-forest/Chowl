package com.chyzman.chowl.block.button;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Direction;

public interface ButtonRenderCondition {
    ButtonRenderCondition ALWAYS = (blockEntity, face, blockTargeted, panelTargeted, buttonTargeted) -> true;
    ButtonRenderCondition BLOCK_FOCUSED = (blockEntity, face, blockTargeted, panelTargeted, buttonTargeted) -> blockTargeted;
    ButtonRenderCondition PANEL_FOCUSED = (blockEntity, face, blockTargeted, panelTargeted, buttonTargeted) -> panelTargeted;
    ButtonRenderCondition BUTTON_FOCUSED = (blockEntity, face, blockTargeted, panelTargeted, buttonTargeted) -> buttonTargeted;
    ButtonRenderCondition NEVER = (blockEntity, face, blockTargeted, panelTargeted, buttonTargeted) -> false;

    boolean shouldRender(BlockEntity blockEntity, Direction side, boolean blockTargeted, boolean panelTargeted, boolean buttonTargeted);
}
