package com.chyzman.chowl.screen;

import com.chyzman.chowl.item.*;
import io.wispforest.owo.client.screens.ScreenUtils;
import io.wispforest.owo.client.screens.SlotGenerator;
import io.wispforest.owo.client.screens.SyncedProperty;
import io.wispforest.owo.ops.ItemOps;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

import java.math.BigInteger;

import static com.chyzman.chowl.item.DrawerPanelItem.COMPONENT;

public class PanelConfigSreenHandler extends ScreenHandler {

    final SyncedProperty<ItemStack> stack;
    public PlayerInventory inventory;

    public static final ScreenHandlerType<PanelConfigSreenHandler> TYPE = new ExtendedScreenHandlerType<>(PanelConfigSreenHandler::new);

    public PanelConfigSreenHandler(int syncId, PlayerInventory playerInventory, ItemStack stack) {
        super(TYPE, syncId);
        this.inventory = playerInventory;
        this.stack = this.createProperty(ItemStack.class, stack);
        var component = stack.get(COMPONENT);
        this.addServerboundMessage(ConfigFilter.class, (message) -> {
            if (component.count.compareTo(BigInteger.ZERO) <= 0) {
                component.setVariant(ItemVariant.of(message.stack));
                component.config.locked = true;
                stack.put(COMPONENT, component);
                this.stack.markDirty();
            }
        });
        this.addServerboundMessage(ConfigConfig.class, (message) -> {
            component.config.locked = message.locked;
            component.config.hideCount = !message.showCount;
            component.config.hideItem = !message.showItem;
            component.config.hideName = !message.showName;
            if (!message.locked && component.count.compareTo(BigInteger.ZERO) <= 0) {
                component.setVariant(ItemVariant.blank());
            }
            stack.put(COMPONENT, component);
            this.stack.markDirty();
        });
        SlotGenerator.begin(this::addSlot, 8, 84).slotFactory(
                (inventory1, index, x, y) -> new DoubleValitatingSlot(inventory1, index, x, y,
                        stack1 -> true,
                        stack1 -> !(stack1.equals(stack)))
        ).playerInventory(playerInventory);
    }

    public PanelConfigSreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, buf.readItemStack());
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ScreenUtils.handleSlotTransfer(this, slot, 0);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public record ConfigFilter(ItemStack stack) {
    }

    public record ConfigConfig(Boolean locked, Boolean showCount, Boolean showItem, Boolean showName) {
    }
}