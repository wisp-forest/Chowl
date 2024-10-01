package com.chyzman.chowl.industries.item.component;

import com.chyzman.chowl.industries.block.button.BlockButton;
import com.chyzman.chowl.industries.block.button.ButtonRenderCondition;
import com.chyzman.chowl.industries.block.button.ButtonRenderer;
import com.chyzman.chowl.industries.event.UpgradeInteractionEvents;
import com.chyzman.chowl.core.registry.ChowlComponents;
import com.chyzman.chowl.industries.screen.UpgradesInventory;
import io.wispforest.owo.ops.ItemOps;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public interface UpgradeablePanelItem extends DisplayingPanelItem {
    default UpgradeListComponent upgrades(ItemStack stack) {
        return stack.getOrDefault(ChowlComponents.UPGRADE_LIST, UpgradeListComponent.DEFAULT);
    }

    @Deprecated(forRemoval = true)
    default void setUpgrades(ItemStack stack, List<ItemStack> upgrades) {
        stack.set(ChowlComponents.UPGRADE_LIST, new UpgradeListComponent(Collections.unmodifiableList(upgrades)));
    }

    default void modifyUpgrades(ItemStack stack, UnaryOperator<List<ItemStack>> modifier) {
        var stacks = stack.getOrDefault(ChowlComponents.UPGRADE_LIST, UpgradeListComponent.DEFAULT).copyStacks();
        stacks = modifier.apply(stacks);
        stack.set(ChowlComponents.UPGRADE_LIST, new UpgradeListComponent(Collections.unmodifiableList(stacks)));
    }

    default boolean hasUpgrade(ItemStack stack, Predicate<ItemStack> upgrade) {
        for (var upgradeStack : upgrades(stack).upgradeStacks()) {
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
                    .onUse((world, frame, useSide, useStack, player) -> {
                        var stackInHand = player.getStackInHand(Hand.MAIN_HAND);
                        if (stackInHand.isEmpty() || !stackInHand.getItem().canBeNested()) return ActionResult.PASS;
                        if (!(useStack.getItem() instanceof PanelItem)) return ActionResult.PASS;
                        if (!upgrades.get(finalI).isEmpty()) return ActionResult.FAIL;

                        var upgrade = ItemOps.singleCopy(stackInHand);
                        stackInHand.decrement(1);

                        if (world.isClient) return ActionResult.SUCCESS;

                        UpgradeInteractionEvents.UPGRADE_INSERTED.invoker().onUpgradeInserted((ServerPlayerEntity) player, frame, useSide, useStack, upgrade);

                        useStack.set(ChowlComponents.UPGRADE_LIST, upgrades.set(finalI, upgrade));
                        frame.stacks.set(useSide.getId(), frame.stacks.get(useSide.getId()).withStack(useStack));
                        frame.markDirty();

                        return ActionResult.SUCCESS;
                    })
                    .onAttack((world, frame, attackedSide, attackedStack, player) -> {
                        if (upgrades.get(finalI).isEmpty()) return ActionResult.FAIL;
                        if (world.isClient) return ActionResult.SUCCESS;

                        var upgrade = upgrades.get(finalI);

                        UpgradeInteractionEvents.UPGRADE_EXTRACTED.invoker().onUpgradeExtracted((ServerPlayerEntity) player, frame, attackedSide, attackedStack, upgrade);

                        player.getInventory().offerOrDrop(upgrade);

                        attackedStack.set(ChowlComponents.UPGRADE_LIST, upgrades.set(finalI, ItemStack.EMPTY));
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

    default UpgradesInventory getUpgradesInventory(ItemStack stack) {
        var inventory = new UpgradesInventory();
        for (int i = 0; i < 8; i++) {
            inventory.setStack(i, upgrades(stack).get(i));
        }
        inventory.addListener((inv) -> modifyUpgrades(stack, stacks -> {
            for (int i = 0; i < 8; i++) {
                stacks.set(i, inv.getStack(i));
            }
            return stacks;
        }));
        return inventory;
    }
}
