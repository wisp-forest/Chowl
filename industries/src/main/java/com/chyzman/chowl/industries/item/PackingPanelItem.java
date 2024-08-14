package com.chyzman.chowl.industries.item;

import com.chyzman.chowl.industries.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.industries.block.DrawerFrameSideState;
import com.chyzman.chowl.industries.block.button.BlockButton;
import com.chyzman.chowl.industries.item.component.*;
import com.chyzman.chowl.industries.registry.ChowlComponents;
import com.chyzman.chowl.industries.registry.ChowlStats;
import com.chyzman.chowl.industries.transfer.BigSingleSlotStorage;
import com.chyzman.chowl.industries.transfer.PanelStorage;
import com.chyzman.chowl.industries.transfer.PanelStorageContext;
import com.chyzman.chowl.industries.util.VariantUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.chyzman.chowl.industries.Chowl.*;

public class PackingPanelItem extends BasePanelItem implements PanelItem, DisplayingPanelItem, UpgradeablePanelItem, CapacityLimitedPanelItem {
    public PackingPanelItem(Settings settings) {
        super(settings);
    }

    public @Nullable SlottedStorage<ItemVariant> getStorage(PanelStorageContext ctx) {
        var items = ctx.stack().getOrDefault(ChowlComponents.BARE_ITEMS, BareItemsComponent.DEFAULT);
        if (items.entries().isEmpty()) {
            return new Storage(ctx, Items.AIR);
        } else {
            List<Storage> storages = new ArrayList<>(items.entries().size());

            for (Item item : items.entries().keySet()) storages.add(new Storage(ctx, item));

            return new CombinedSlottedStorage<>(storages);
        }
    }

    @Override
    public List<BlockButton> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        var returned = new ArrayList<BlockButton>();

        var items = stack.getOrDefault(ChowlComponents.BARE_ITEMS, BareItemsComponent.DEFAULT);
        var stacks = items.entries().entrySet().stream().toList();

        //TODO abstract this away because its very similar to other panels just a grid
        var gridSize = Math.ceil(Math.sqrt(stacks.size()));
        for (int i = 0; i < gridSize * gridSize; i++) {
            var scale = 12 / gridSize;
            float x = (float) (scale * (i % gridSize));
            float y = (float) (scale * (gridSize - 1 - (float) (int) (i / gridSize)));
            int finalI = i;
            returned.add(PanelItem.buttonBuilder(2 + x, 2 + y, (float) (2 + x + scale), (float) (2 + y + scale))
                    .onUse((world, frame, useSide, useStack, player) -> {
                        var stackInHand = player.getStackInHand(player.getActiveHand());
                        if (stackInHand.isEmpty()) return ActionResult.PASS;
                        if (!(stack.getItem() instanceof PanelItem panel)) return ActionResult.PASS;

                        if (world.isClient) return ActionResult.SUCCESS;

                        var storage = panel.getStorage(PanelStorageContext.from(frame, side));

                        try (var tx = Transaction.openOuter()) {
                            long moved = StorageUtil.move(
                                    PlayerInventoryStorage.of(player).getHandSlot(player.getActiveHand()),
                                    storage,
                                    variant -> true,
                                    stackInHand.getCount(),
                                    tx
                            );
                            player.increaseStat(ChowlStats.ITEMS_INSERTED_STAT, (int) moved);

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
                                var resource = ItemVariant.of(stacks.get(finalI).getKey());

                                if (resource != null) {
                                    var extracted = storage.extract(resource, player.isSneaking() ? resource.toStack().getMaxCount() : 1, tx);

                                    if (extracted > 0) {
                                        PlayerInventoryStorage.of(player).offerOrDrop(resource, extracted, tx);
                                        player.increaseStat(ChowlStats.ITEMS_EXTRACTED_STAT, (int) extracted);
                                        tx.commit();
                                        return ActionResult.SUCCESS;
                                    }
                                }
                            }
                            if (stack.getOrDefault(ChowlComponents.COUNT, BigInteger.ZERO).compareTo(BigInteger.ZERO) > 0) return ActionResult.FAIL;
                        }


                        player.getInventory().offerOrDrop(stack);
                        drawerFrame.stacks.set(side.getId(), DrawerFrameSideState.empty());
                        drawerFrame.markDirty();
                        return ActionResult.SUCCESS;
                    })
                    .onDoubleClick((world, clickedFrame, clickedSide, clickedStack, player) -> {
                        var storage = getStorage(PanelStorageContext.from(clickedFrame, side));

                        if (storage == null) return ActionResult.FAIL;
//                        if (currentFilter(stack).isBlank()) return ActionResult.FAIL;
                        if (world.isClient) return ActionResult.SUCCESS;

                        try (var tx = Transaction.openOuter()) {
                            long moved = StorageUtil.move(PlayerInventoryStorage.of(player), storage, variant -> true, Long.MAX_VALUE, tx);
                            player.increaseStat(ChowlStats.ITEMS_INSERTED_STAT, (int) moved);

                            tx.commit();

                            return ActionResult.SUCCESS;
                        }
                    }).build()
            );
        }
        return returned;
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
        return new BigInteger(CHOWL_CONFIG.base_panel_capacity());
    }

    private class Storage extends PanelStorage implements BigSingleSlotStorage<ItemVariant> {
        private final Item item;

        public Storage(PanelStorageContext ctx, Item item) {
            super(ctx);
            this.item = item;
        }

        @Override
        public BigInteger bigInsert(ItemVariant resource, BigInteger maxAmount, TransactionContext transaction) {
            if (VariantUtils.hasNbt(resource)) return BigInteger.ZERO;

            var items = ctx.stack().getOrDefault(ChowlComponents.BARE_ITEMS, BareItemsComponent.DEFAULT);

            var currentCount = items.totalCount();
            var capacity = PackingPanelItem.this.capacity(ctx.stack());
            var spaceLeft = capacity.subtract(currentCount).max(BigInteger.ZERO);
            var inserted = spaceLeft.min(maxAmount);

            updateSnapshots(transaction);
            ctx.stack().set(ChowlComponents.BARE_ITEMS, items.copyAndInsert(resource.getItem(), inserted));

            if (PackingPanelItem.this.hasUpgrade(
                    ctx.stack(),
                    upgrade -> upgrade.isIn(VOID_UPGRADE_TAG)
                            || (!VariantUtils.isFireproof(resource) && upgrade.isIn(LAVA_UPGRADE_TAG))
            ))
                return maxAmount;

            return inserted;
        }

        @Override
        public BigInteger bigExtract(ItemVariant resource, BigInteger maxAmount, TransactionContext tx) {
            if (VariantUtils.hasNbt(resource)) return BigInteger.ZERO;

            var items = ctx.stack().getOrDefault(ChowlComponents.BARE_ITEMS, BareItemsComponent.DEFAULT);

            var currentCount = items.entries().getOrDefault(resource.getItem(), BigInteger.ZERO);

            BigInteger removed = currentCount.min(maxAmount);
            var newCount = currentCount.subtract(removed);

            updateSnapshots(tx);
            ctx.stack().set(ChowlComponents.BARE_ITEMS, items.copyAndSet(resource.getItem(), newCount));

            if (items.entries().isEmpty()) {
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
            return ItemVariant.of(item);
        }

        @Override
        public BigInteger bigAmount() {
            return ctx.stack().getOrDefault(ChowlComponents.BARE_ITEMS, BareItemsComponent.DEFAULT).entries().getOrDefault(item, BigInteger.ZERO);
        }

        @Override
        public BigInteger bigCapacity() {
            return PackingPanelItem.this.capacity(ctx.stack());
        }
    }
}
