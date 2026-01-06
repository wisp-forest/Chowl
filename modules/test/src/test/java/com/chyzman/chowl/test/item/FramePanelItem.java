package com.chyzman.chowl.test.item;

import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import com.chyzman.chowl.core.multipart.api.Multipart;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.registry.CoreBlocks;
import com.chyzman.chowl.test.multipart.FramePanel;
import com.chyzman.chowl.test.registry.TestParts;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class FramePanelItem extends Item implements Multipart<FramePanel> {
    public FramePanelItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockState state = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (state.is(CoreBlocks.DRAWER_FRAME) && blockEntity instanceof MultipartBlockEntity entity) {
            FramePanel panel = new FramePanel(context.getClickedFace(), ItemStack.EMPTY, List.of(), 0, 64);
            entity.addPart(panel.init(entity));
        }

        return super.useOn(context);
    }

    @Override
    public PartType<FramePanel> getPart() {
        return TestParts.FRAME_PANEL_PART;
    }
}
