package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.item.component.*;
import com.chyzman.chowl.screen.PanelConfigSreenHandler;
import com.chyzman.chowl.transfer.DrawerPanelStorage;
import com.chyzman.chowl.transfer.TransferState;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class DrawerPanelItem extends Item implements PanelItem, DrawerFilterHolder, DrawerCountHolder, DrawerLockHolder, DrawerCustomizationHolder {

    public DrawerPanelItem(Settings settings) {
        super(settings);
    }

    public @Nullable SlottedStorage<ItemVariant> getStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side) {
        if (TransferState.NO_BLANK_DRAWERS.get() && filter(stack).isBlank()) return null;

        return new DrawerPanelStorage(stack, blockEntity, side);
    }

    @Override
    public List<Button> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        return List.of(STORAGE_BUTTON);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if (user.isSneaking()) {
                var stack = user.getStackInHand(hand);
                var factory = new ExtendedScreenHandlerFactory() {
                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                        return new PanelConfigSreenHandler(syncId, playerInventory, stack);
                    }

                    @Override
                    public Text getDisplayName() {
                        return Text.translatable("container.chowl.panel_config.title");
                    }

                    @Override
                    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                        buf.writeItemStack(user.getStackInHand(hand));
                    }
                };
                user.openHandledScreen(factory);
            }
        }
        return super.use(world, user, hand);
    }
}