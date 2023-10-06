package com.chyzman.chowl.screen;

import com.chyzman.chowl.item.component.DrawerCountHolder;
import com.chyzman.chowl.item.component.DrawerCustomizationHolder;
import com.chyzman.chowl.item.component.DrawerFilterHolder;
import com.chyzman.chowl.item.component.DrawerLockHolder;
import io.wispforest.owo.client.screens.ScreenUtils;
import io.wispforest.owo.client.screens.SlotGenerator;
import io.wispforest.owo.client.screens.SyncedProperty;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Colors;

import java.math.BigInteger;

public class PanelConfigSreenHandler extends ScreenHandler {

    final SyncedProperty<ItemStack> stack;
    public PlayerInventory inventory;

    public static final ScreenHandlerType<PanelConfigSreenHandler> TYPE = new ExtendedScreenHandlerType<>(PanelConfigSreenHandler::new);

    public PanelConfigSreenHandler(int syncId, PlayerInventory playerInventory, ItemStack stack) {
        super(TYPE, syncId);
        this.inventory = playerInventory;
        this.stack = this.createProperty(ItemStack.class, stack);
        this.addServerboundMessage(ConfigFilter.class, (message) -> {
            var temp = this.stack.get();
            if (message.stack.getItem() instanceof DrawerCountHolder<?> drawerCountHolder) {
                if (drawerCountHolder.count(message.stack).compareTo(BigInteger.ZERO) <= 0) {
                    drawerCountHolder.count(temp, BigInteger.ONE);
                    if (temp.getItem() instanceof DrawerLockHolder<?> lockHolder) lockHolder.locked(temp, true);
                }
                this.stack.set(temp);
                this.stack.markDirty();
            }
        });
        this.addServerboundMessage(ConfigConfig.class, (message) -> {
            var temp = this.stack.get();
            if (temp.getItem() instanceof DrawerCustomizationHolder<?> drawerCustomizationHolder) {
                drawerCustomizationHolder
                        .showCount(temp, message.showCount)
                        .showItem(temp, message.showItem)
                        .showName(temp, message.showName)
                        .textStyle(temp, drawerCustomizationHolder.textStyle(temp));
            }
            if (temp.getItem() instanceof DrawerLockHolder<?> drawerLockHolder) {
                drawerLockHolder.locked(temp, message.locked);
            }
            if (temp.getItem() instanceof DrawerCountHolder<?> drawerCountHolder) {
                if (!message.locked && drawerCountHolder.count(temp).compareTo(BigInteger.ZERO) <= 0) {
                    drawerCountHolder.count(temp, BigInteger.ZERO);
                }
            }
            this.stack.set(temp);
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