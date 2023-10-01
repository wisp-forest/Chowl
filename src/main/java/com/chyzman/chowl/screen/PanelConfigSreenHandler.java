package com.chyzman.chowl.screen;

import com.chyzman.chowl.item.*;
import io.wispforest.owo.client.screens.ScreenUtils;
import io.wispforest.owo.client.screens.SlotGenerator;
import io.wispforest.owo.client.screens.ValidatingSlot;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

import java.math.BigInteger;

import static com.chyzman.chowl.item.DrawerPanelItem.COMPONENT;

public class PanelConfigSreenHandler extends ScreenHandler {

    public SimpleInventory filter;

    public PlayerInventory inventory;
    public ItemStack stack;

    public static final ScreenHandlerType<PanelConfigSreenHandler> TYPE = new ExtendedScreenHandlerType<>(PanelConfigSreenHandler::new);

    public PanelConfigSreenHandler(int syncId, PlayerInventory playerInventory, ItemStack stack) {
        super(TYPE, syncId);
        this.inventory = playerInventory;
        this.stack = stack;
        filter = DrawerPanelItem.createTrackedInventory(stack);
        var component = stack.get(COMPONENT);
        this.addSlot(new PanelFilterSlot(filter, 0, 0, 0,
                stack1 -> !(stack1.getItem() instanceof PanelItem),
                stack1 -> component.count.compareTo(BigInteger.ZERO) <= 0));
        SlotGenerator.begin(this::addSlot, 8, 84).slotFactory(
                (inventory1, index, x, y) -> new DoubleValitatingSlot(inventory1, index, x, y,
                        stack1 -> true,
                        stack1 -> !(stack1.getItem() instanceof PanelItem))
        ).playerInventory(playerInventory);
    }

    public PanelConfigSreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, buf.readItemStack());
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ScreenUtils.handleSlotTransfer(this, slot, 1);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}