package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.screen.PanelConfigSreenHandler;
import com.chyzman.chowl.transfer.DrawerPanelStorage;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.math.BigInteger;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class DrawerPanelItem extends Item implements PanelItem {
    public static final NbtKey<DrawerComponent> COMPONENT = new NbtKey<>("DrawerComponent", DrawerComponent.KEY_TYPE);
    public static final PanelItem.Button DRAWER_BUTTON = new PanelItem.Button(1 / 8f, 1 / 8f, 7 / 8f, 7 / 8f,
            (world, drawerFrame, side, stack, player, hand) -> {
                var stackInHand = player.getStackInHand(hand);
                if (stackInHand.isEmpty()) return ActionResult.PASS;

                DrawerPanelItem panel = (DrawerPanelItem) stack.getItem();

                if (!world.isClient) {
                    panel.insert(stack, stackInHand);
                    drawerFrame.markDirty();
                }

                return ActionResult.SUCCESS;
            },
            (world, drawerFrame, side, stack, player) -> {
                var stacks = drawerFrame.stacks;
                if (!stack.isEmpty()) {
                    DrawerPanelItem panel = (DrawerPanelItem) stack.getItem();

                    if (!panel.getVariant(stack).isBlank()) {
                        if (world.isClient && panel.getCount(stack).signum() > 0) {
                            return ActionResult.SUCCESS;
                        }

                        var extracted = panel.extract(stack, player.isSneaking());
                        if (!extracted.isEmpty()) {
                            player.getInventory().offerOrDrop(extracted);
                            drawerFrame.markDirty();
                            return ActionResult.SUCCESS;
                        }
                    } else {
                        if (world.isClient) return ActionResult.SUCCESS;

                        player.getInventory().offerOrDrop(stack);
                        stacks[side.getId()] = ItemStack.EMPTY;
                        drawerFrame.markDirty();
                        return ActionResult.SUCCESS;
                    }
                }

                return ActionResult.PASS;
            },
            (world, frame, side, stack, player) -> {
                var panel = (DrawerPanelItem) stack.getItem();
                var storage = panel.getStorage(stack, frame, side);

                if (storage == null) return ActionResult.FAIL;
                if (world.isClient) return ActionResult.SUCCESS;

                try (var tx = Transaction.openOuter()) {
                    ItemVariant stored = panel.getVariant(stack);
                    StorageUtil.move(PlayerInventoryStorage.of(player), storage, variant -> variant.equals(stored), Long.MAX_VALUE, tx);

                    tx.commit();

                    return ActionResult.SUCCESS;
                }
            },
            null);

    public DrawerPanelItem(Settings settings) {
        super(settings);
    }

    public void insert(ItemStack stack, ItemStack inserted) {
        var component = stack.get(COMPONENT);
        inserted.setCount(component.insert(inserted));
        stack.put(COMPONENT, component);
    }

    public ItemStack extract(ItemStack stack, boolean sneaking) {
        var component = stack.get(COMPONENT);
        var amount = sneaking ? component.itemVariant.getItem().getMaxCount() : 1;
        var returned = component.extract(amount);

        if (returned.isEmpty()) return returned;

        stack.put(COMPONENT, component);
        return returned;
    }

    public BigInteger getCount(ItemStack stack) {
        var component = stack.get(COMPONENT);
        return component.count;
    }

    public ItemVariant getVariant(ItemStack stack) {
        var component = stack.get(COMPONENT);
        return component.itemVariant;
    }

    @SuppressWarnings("UnstableApiUsage")
    public SlottedStorage<ItemVariant> getStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side) {
        return new DrawerPanelStorage(stack, blockEntity, side);
    }

    @Override
    public List<Button> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        return List.of(DRAWER_BUTTON);
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

    public static SimpleInventory createTrackedInventory(ItemStack stack) {
        var inventory = new SimpleInventory(1);
        var component = stack.get(COMPONENT);
        inventory.setStack(0, component.itemVariant.toStack(1));

        inventory.addListener(sender -> storeInventory(stack, inventory));
        return inventory;
    }

    public static void storeInventory(ItemStack stack, SimpleInventory inventory) {
        var component = stack.get(COMPONENT);
        if (!component.config.locked || component.count.compareTo(BigInteger.ZERO) <= 0) {
            component.setVariant(ItemVariant.of(inventory.getStack(0)));
            stack.put(COMPONENT, component);
        }
    }
}