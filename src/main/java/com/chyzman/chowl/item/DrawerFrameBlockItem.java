package com.chyzman.chowl.item;

import com.chyzman.chowl.network.C2SConfigPanel;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static com.chyzman.chowl.Chowl.CHANNEL;
import static com.chyzman.chowl.item.DrawerPanelItem.COMPONENT;

public class DrawerFrameBlockItem extends BlockItem {
    public DrawerFrameBlockItem(Block block, Settings settings) {
        super(block, settings);
    }
}