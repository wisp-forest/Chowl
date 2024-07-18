package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.block.button.BlockButton;
import com.chyzman.chowl.item.component.*;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.transfer.*;
import com.chyzman.chowl.util.CompressionManager;
import com.chyzman.chowl.util.VariantUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.chyzman.chowl.Chowl.*;

@SuppressWarnings("UnstableApiUsage")
public class CompressingPanelItem extends BasePanelItem implements FilteringPanelItem, LockablePanelItem, DisplayingPanelItem, StoragePanelItem, UpgradeablePanelItem {
    public CompressingPanelItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ItemVariant currentFilter(ItemStack stack) {
        return ItemVariant.of(stack.getOrDefault(ChowlRegistry.CONTAINED_ITEM, Items.AIR));
    }

    @Override
    public boolean canSetFilter(ItemStack stack, ItemVariant to) {
        if (!to.getComponents().isEmpty()) return false;

        return stack.getOrDefault(ChowlRegistry.COUNT, BigInteger.ZERO).signum() == 0;
    }

    @Override
    public void setFilter(ItemStack stack, ItemVariant newFilter) {
        var baseNew = CompressionManager.followDown(newFilter.getItem()).item();

        stack.set(ChowlRegistry.CONTAINED_ITEM, baseNew);
        stack.set(ChowlRegistry.LOCKED, baseNew != Items.AIR);
    }

    @Override
    public boolean locked(ItemStack stack) {
        return stack.getOrDefault(ChowlRegistry.LOCKED, false);
    }

    @Override
    public void setLocked(ItemStack stack, boolean locked) {
        stack.set(ChowlRegistry.LOCKED, locked);

        if (!locked && stack.getOrDefault(ChowlRegistry.COUNT, BigInteger.ZERO).equals(BigInteger.ZERO)) {
            stack.set(ChowlRegistry.CONTAINED_ITEM, Items.AIR);
        }
    }

    @Override
    public List<BlockButton> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        var returned = new ArrayList<BlockButton>();
        var stacks = new ArrayList<ItemStack>();

        stacks.add(new ItemStack(stack.getOrDefault(ChowlRegistry.CONTAINED_ITEM, Items.AIR)));
        var node = CompressionManager.getOrCreateNode(stack.getOrDefault(ChowlRegistry.CONTAINED_ITEM, Items.AIR));
        while (node.next != null) {
            node = node.next;
            stacks.add(node.item.getDefaultStack());
        }

        var gridSize = Math.ceil(Math.sqrt(stacks.size()));
        for (int i = 0; i < gridSize * gridSize; i++) {
            var scale = 12 / gridSize;
            float x = (float) (scale * (i % gridSize));
            float y = (float) (scale * (gridSize - 1 - (float) (int) (i / gridSize)));
            int finalI = i;
            returned.add(PanelItem.buttonBuilder(2 + x, 2 + y, (float) (2 + x + scale), (float) (2 + y + scale))
                    .onUse((world, frame, useSide, useStack, player) -> {
                        var stackInHand = player.getStackInHand(Hand.MAIN_HAND);
                        if (stackInHand.isEmpty()) return ActionResult.PASS;
                        if (!(stack.getItem() instanceof PanelItem panel)) return ActionResult.PASS;

                        if (world.isClient) return ActionResult.SUCCESS;

                        var storage = panel.getStorage(PanelStorageContext.from(frame, side));

                        try (var tx = Transaction.openOuter()) {
                            long moved = StorageUtil.move(
                                    PlayerInventoryStorage.of(player).getHandSlot(Hand.MAIN_HAND),
                                    storage,
                                    variant -> true,
                                    stackInHand.getCount(),
                                    tx
                            );
                            player.increaseStat(ChowlRegistry.ITEMS_INSERTED_STAT, (int) moved);

                            tx.commit();
                        }

                        return ActionResult.SUCCESS;
                    })
                    .onAttack((world, attackedDrawerFrame, attackedSide, attackedStack, player) -> {
                        if (stacks.size() <= finalI) return ActionResult.FAIL;
                        if (canExtractFromButton()) {
                            var storage = getStorage(PanelStorageContext.from(drawerFrame, side));

                            if (storage == null) return ActionResult.FAIL;
                            if (world.isClient) return ActionResult.SUCCESS;

                            try (var tx = Transaction.openOuter()) {
                                var resource = ItemVariant.of(stacks.get(finalI));

                                if (resource != null) {
                                    var extracted = storage.extract(resource, player.isSneaking() ? resource.toStack().getMaxCount() : 1, tx);

                                    if (extracted > 0) {
                                        PlayerInventoryStorage.of(player).offerOrDrop(resource, extracted, tx);
                                        player.increaseStat(ChowlRegistry.ITEMS_EXTRACTED_STAT, (int) extracted);
                                        tx.commit();
                                        return ActionResult.SUCCESS;
                                    }
                                }
                            }
                            if (stack.getOrDefault(ChowlRegistry.COUNT, BigInteger.ZERO).compareTo(BigInteger.ZERO) > 0) return ActionResult.FAIL;
                        }


                        player.getInventory().offerOrDrop(stack);
                        drawerFrame.stacks.set(side.getId(), DrawerFrameBlockEntity.SideState.empty());
                        drawerFrame.markDirty();
                        return ActionResult.SUCCESS;
                    })
                    .onDoubleClick((world, clickedFrame, clickedSide, clickedStack, player) -> {
                        var storage = getStorage(PanelStorageContext.from(clickedFrame, side));

                        if (storage == null) return ActionResult.FAIL;
                        if (currentFilter(stack).isBlank()) return ActionResult.FAIL;
                        if (world.isClient) return ActionResult.SUCCESS;

                        try (var tx = Transaction.openOuter()) {
                            long moved = StorageUtil.move(PlayerInventoryStorage.of(player), storage, variant -> true, Long.MAX_VALUE, tx);
                            player.increaseStat(ChowlRegistry.ITEMS_INSERTED_STAT, (int) moved);

                            tx.commit();

                            return ActionResult.SUCCESS;
                        }
                    }).build()
            );
        }
        return returned;
    }

    @Override
    public @Nullable SlottedStorage<ItemVariant> getStorage(PanelStorageContext ctx) {
        var storages = new ArrayList<SlottedStorage<ItemVariant>>();
        var base = new BaseStorage(ctx);

        storages.add(base);

        int steps = CompressionManager.followUp(base.getResource().getItem()).totalSteps();
        for (int i = 0; i < steps; i++) {
            storages.add(new CompressingStorage(base, i + 1));
        }

        if (steps == 0) {
            storages.add(new InitialCompressingStorage(base));
        }

        return new CombinedSlottedStorage<>(storages);
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public boolean hasComparatorOutput() {
        return true;
    }

    @Override
    public BigInteger baseCapacity() {
        return new BigInteger(CHOWL_CONFIG.base_compressing_panel_capacity());
    }

    @Override
    public BigInteger capacity(ItemStack panel) {
        return StoragePanelItem.super.capacity(panel);
    }

    @Override
    public BigInteger fullCapacity(ItemStack stack) {
        return capacity(stack).multiply(CompressionManager.followUp(stack.getOrDefault(ChowlRegistry.CONTAINED_ITEM, Items.AIR)).totalMultiplier());
    }

    @Override
    public BigInteger count(ItemStack stack) {
        return stack.getOrDefault(ChowlRegistry.COUNT, BigInteger.ZERO);
    }

    @Override
    public void setCount(ItemStack stack, BigInteger count) {
        stack.set(ChowlRegistry.COUNT, count);
    }

    @SuppressWarnings("UnstableApiUsage")
    private class BaseStorage extends PanelStorage implements BigSingleSlotStorage<ItemVariant> {
        public BaseStorage(PanelStorageContext ctx) {
            super(ctx);
        }

        @Override
        public BigInteger bigInsert(ItemVariant resource, BigInteger maxAmount, TransactionContext transaction) {
            if (VariantUtils.hasNbt(resource)) return BigInteger.ZERO;
            if (CompressionManager.getOrCreateNode(resource.getItem()).previous != null) return BigInteger.ZERO;

            var contained = ctx.stack().getOrDefault(ChowlRegistry.CONTAINED_ITEM, Items.AIR);

            if (contained == Items.AIR) contained = resource.getItem();
            if (contained != resource.getItem()) return BigInteger.ZERO;

            updateSnapshots(transaction);
            ctx.stack().set(ChowlRegistry.CONTAINED_ITEM, contained);

            var currentCount = ctx.stack().getOrDefault(ChowlRegistry.COUNT, BigInteger.ZERO);
            var capacity = bigCapacity();
            var spaceLeft = capacity.subtract(currentCount).max(BigInteger.ZERO);
            var inserted = spaceLeft.min(maxAmount);

            ctx.stack().set(ChowlRegistry.COUNT, currentCount.add(inserted));

            Item finalContained = contained;

            if (CompressingPanelItem.this.hasUpgrade(
                    ctx.stack(),
                    upgrade -> upgrade.isIn(VOID_UPGRADE_TAG)
                            || (!finalContained.getComponents().contains(DataComponentTypes.FIRE_RESISTANT) && upgrade.isIn(LAVA_UPGRADE_TAG))
            ))
                return maxAmount;

            return inserted;
        }

        @Override
        public BigInteger bigExtract(ItemVariant resource, BigInteger maxAmount, TransactionContext tx) {
            if (VariantUtils.hasNbt(resource)) return BigInteger.ZERO;

            var contained = ctx.stack().getOrDefault(ChowlRegistry.CONTAINED_ITEM, Items.AIR);

            if (contained == Items.AIR) return BigInteger.ZERO;
            if (contained != resource.getItem()) return BigInteger.ZERO;

            var currentCount = ctx.stack().getOrDefault(ChowlRegistry.COUNT, BigInteger.ZERO);

            BigInteger removed = currentCount.min(maxAmount);
            var newCount = currentCount.subtract(removed);

            updateSnapshots(tx);
            ctx.stack().set(ChowlRegistry.COUNT, newCount);

            if (newCount.equals(BigInteger.ZERO)) {
                if (!ctx.stack().getOrDefault(ChowlRegistry.LOCKED, false)) {
                    ctx.stack().set(ChowlRegistry.CONTAINED_ITEM, Items.AIR);
                }

                needsEmptiedEvent = true;
            }

            return removed;
        }

        @Override
        public boolean isResourceBlank() {
            return getResource().isBlank();
        }

        @Override
        public ItemVariant getResource() {
            return ItemVariant.of(ctx.stack().getOrDefault(ChowlRegistry.CONTAINED_ITEM, Items.AIR));
        }

        @Override
        public BigInteger bigAmount() {
            return ctx.stack().getOrDefault(ChowlRegistry.COUNT, BigInteger.ZERO);
        }

        @Override
        public BigInteger bigCapacity() {
            return CompressingPanelItem.this.fullCapacity(ctx.stack());
        }
    }
}