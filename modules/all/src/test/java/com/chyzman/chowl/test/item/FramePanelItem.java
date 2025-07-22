package com.chyzman.chowl.test.item;

import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import com.chyzman.chowl.core.multipart.api.Multipart;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.registry.CoreBlocks;
import com.chyzman.chowl.test.multipart.FramePanel;
import com.chyzman.chowl.test.registry.TestParts;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class FramePanelItem extends Item implements Multipart<FramePanel> {
    public FramePanelItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();

        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (state.isOf(CoreBlocks.DRAWER_FRAME) && blockEntity instanceof MultipartBlockEntity entity) {
            FramePanel panel = new FramePanel(context.getSide(), ItemStack.EMPTY, List.of(), 0, 64);
            entity.addPart(panel.init(entity));
        }

        return super.useOnBlock(context);
    }

    @Override
    public PartType<FramePanel> getPart() {
        return TestParts.FRAME_PANEL_PART;
    }
}
