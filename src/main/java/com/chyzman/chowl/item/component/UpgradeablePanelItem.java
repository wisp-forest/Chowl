package com.chyzman.chowl.item.component;

import com.chyzman.chowl.block.button.BlockButton;
import com.chyzman.chowl.block.button.ButtonRenderCondition;
import com.chyzman.chowl.block.button.ButtonRenderer;
import com.chyzman.chowl.event.UpgradeInteractionEvents;
import io.wispforest.owo.ops.ItemOps;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import io.wispforest.owo.serialization.format.nbt.NbtEndec;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface UpgradeablePanelItem extends DisplayingPanelItem {
    KeyedEndec<NbtList> UPGRADES_LIST = NbtEndec.ELEMENT.xmap(e -> (NbtList) e, e -> e).keyed("Upgrades", new NbtList());

    default List<ItemStack> upgrades(ItemStack stack) {
        var returned = new ArrayList<ItemStack>();

        if (stack.has(UPGRADES_LIST))
            stack.get(UPGRADES_LIST).forEach(nbtElement -> returned.add(ItemStack.fromNbt((NbtCompound) nbtElement)));

        while (returned.size() < 8) returned.add(ItemStack.EMPTY);
        return returned;
    }

    default void setUpgrades(ItemStack stack, List<ItemStack> upgrades) {
        var nbtList = new NbtList();
        upgrades.forEach(itemStack -> nbtList.add(itemStack.writeNbt(new NbtCompound())));
        stack.put(UPGRADES_LIST, nbtList);
    }

    default boolean hasUpgrade(ItemStack stack, Predicate<ItemStack> upgrade) {
        for (var upgradeStack : upgrades(stack)) {
            if (upgrade.test(upgradeStack)) {
                return true;
            }
        }
        return false;
    }

    default void addUpgradeButtons(ItemStack stack, List<BlockButton> buttonList) {
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            var upgrades = upgrades(stack);

            buttonList.add(PanelItem.buttonBuilder(i * 2, 0, (i + 1) * 2, 2)
                    .onUse((world, frame, useSide, useStack, player, hand) -> {
                        var stackInHand = player.getStackInHand(hand);
                        if (stackInHand.isEmpty() || !stackInHand.getItem().canBeNested()) return ActionResult.PASS;
                        if (!(useStack.getItem() instanceof PanelItem)) return ActionResult.PASS;
                        if (upgrades.get(finalI).isEmpty()) {
                            var upgrade = ItemOps.singleCopy(stackInHand);
                            stackInHand.decrement(1);
                            if (world.isClient) return ActionResult.SUCCESS;
                            upgrades.set(finalI, upgrade);

                            UpgradeInteractionEvents.UPGRADE_INSERTED.invoker().onUpgradeInserted((ServerPlayerEntity) player, frame, useSide, useStack, upgrade);
                        } else {
                            return ActionResult.FAIL;
                        }

                        setUpgrades(useStack, upgrades);
                        frame.stacks.set(useSide.getId(), frame.stacks.get(useSide.getId()).withStack(useStack));
                        frame.markDirty();

                        return ActionResult.SUCCESS;
                    })
                    .onAttack((world, frame, attackedSide, attackedStack, player) -> {
                        if (!upgrades.get(finalI).isEmpty()) {
                            var upgrade = upgrades.get(finalI);
                            if (world.isClient) return ActionResult.SUCCESS;
                            upgrades.set(finalI, ItemStack.EMPTY);
                            UpgradeInteractionEvents.UPGRADE_EXTRACTED.invoker().onUpgradeExtracted((ServerPlayerEntity) player, frame, attackedSide, attackedStack, upgrade);
                            player.getInventory().offerOrDrop(upgrade);
                        } else {
                            return ActionResult.FAIL;
                        }
                        setUpgrades(attackedStack, upgrades);
                        frame.stacks.set(attackedSide.getId(), frame.stacks.get(attackedSide.getId()).withStack(attackedStack));
                        frame.markDirty();
                        return ActionResult.SUCCESS;
                    })
                    .renderWhen(ButtonRenderCondition.PANEL_FOCUSED)
                    .renderer(ButtonRenderer.stack(upgrades.get(finalI)))
                    .build()
            );
        }
    }
}