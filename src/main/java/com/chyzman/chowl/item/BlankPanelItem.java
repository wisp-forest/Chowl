package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.screen.PanelConfigSreenHandler;
import com.chyzman.chowl.transfer.DrawerPanelStorage;
import com.chyzman.chowl.transfer.TransferState;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class BlankPanelItem extends Item implements PanelItem {
    public BlankPanelItem(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable SlottedStorage<ItemVariant> getStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side) {
        return null;
    }
}