package com.chyzman.chowl.item;

import com.chyzman.chowl.block.BlockButtonProvider;
import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.transfer.DrawerPanelStorage;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;

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
                    var extracted = panel.extract(stack, player.isSneaking());
                    if (!extracted.isEmpty()) {
                        player.getInventory().offerOrDrop(extracted);
                        drawerFrame.markDirty();
                        return ActionResult.SUCCESS;
                    }
                } else {
                    player.getInventory().offerOrDrop(stack);
                    stacks[side.getId()] = ItemStack.EMPTY;
                    drawerFrame.markDirty();
                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.PASS;
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
}