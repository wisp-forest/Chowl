package com.chyzman.chowl.item.component;

import com.chyzman.chowl.block.button.BlockButton;
import com.chyzman.chowl.block.button.ButtonRenderCondition;
import com.chyzman.chowl.block.button.ButtonRenderer;
import com.chyzman.chowl.transfer.PanelStorageContext;
import io.wispforest.owo.nbt.NbtKey;
import io.wispforest.owo.ops.ItemOps;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static com.chyzman.chowl.Chowl.EXPLOSIVE_UPGRADE_TAG;
import static com.chyzman.chowl.Chowl.FIERY_UPGRADE_TAG;

public interface UpgradeablePanelItem extends PanelItem {
    NbtKey.ListKey<ItemStack> UPGRADES_LIST = new NbtKey.ListKey<>("Upgrades", NbtKey.Type.ITEM_STACK);

    List<ItemStack> upgrades(ItemStack stack);

    void setUpgrades(ItemStack stack, List<ItemStack> upgrades);

    default boolean hasUpgrade(ItemStack stack, Predicate<ItemStack> upgrade) {
        for (var upgradeStack : upgrades(stack)) {
            if (upgrade.test(upgradeStack)) {
                return true;
            }
        }
        return false;
    }

    default List<BlockButton> addUpgradeButtons(ItemStack stack, ArrayList<BlockButton> buttonList) {
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            var upgrades = upgrades(stack);

            buttonList.add(PanelItem.buttonBuilder(i * 2, 0, (i + 1) * 2, 2)
                    .onUse((world, frame, useSide, useStack, player, hand) -> {
                        var stackInHand = player.getStackInHand(hand);
                        if (stackInHand.isEmpty()) return ActionResult.PASS;
                        if (!(useStack.getItem() instanceof PanelItem)) return ActionResult.PASS;
                        if (upgrades.get(finalI).isEmpty()) {
                            var upgrade = ItemOps.singleCopy(stackInHand);
                            stackInHand.decrement(1);
                            if (world.isClient) return ActionResult.SUCCESS;
                            upgrades.set(finalI, upgrade);
                        } else {
                            return ActionResult.FAIL;
                        }

                        setUpgrades(useStack, upgrades);
                        frame.stacks.set(useSide.getId(), new Pair<>(useStack, frame.stacks.get(useSide.getId()).getRight()));
                        frame.markDirty();

                        return ActionResult.SUCCESS;
                    })
                    .onAttack((world, attackedDrawerFrame, attackedSide, attackedStack, player) -> {
                        if (!upgrades.get(finalI).isEmpty()) {
                            var upgrade = upgrades.get(finalI);
                            if (world.isClient) return ActionResult.SUCCESS;
                            upgrades.set(finalI, ItemStack.EMPTY);
                            player.getInventory().offerOrDrop(upgrade);
                        } else {
                            return ActionResult.FAIL;
                        }
                        setUpgrades(attackedStack, upgrades);
                        attackedDrawerFrame.stacks.set(attackedSide.getId(), new Pair<>(attackedStack, attackedDrawerFrame.stacks.get(attackedSide.getId()).getRight()));
                        attackedDrawerFrame.markDirty();
                        return ActionResult.SUCCESS;
                    })
                    .renderWhen(ButtonRenderCondition.PANEL_FOCUSED)
                    .renderer(ButtonRenderer.stack(upgrades.get(finalI)))
                    .build()
            );
        }
        return buttonList;
    }
}