package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.item.component.*;
import com.chyzman.chowl.transfer.PanelStorage;
import com.chyzman.chowl.transfer.TransferState;
import com.chyzman.chowl.util.BigIntUtils;
import com.chyzman.chowl.util.NbtKeyTypes;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.List;

import static com.chyzman.chowl.Chowl.CHOWL_CONFIG;

@SuppressWarnings("UnstableApiUsage")
public class DrawerPanelItem extends BasePanelItem implements PanelItem, FilteringPanelItem, LockablePanelItem, DisplayingPanelItem {
    public static final NbtKey<ItemVariant> VARIANT = new NbtKey<>("Variant", NbtKeyTypes.ITEM_VARIANT);
    public static final NbtKey<BigInteger> COUNT = new NbtKey<>("Count", NbtKeyTypes.BIG_INTEGER);
    public static final NbtKey<BigInteger> CAPACITY = new NbtKey<>("Capacity", NbtKeyTypes.BIG_INTEGER);
    public static final NbtKey<Boolean> LOCKED = new NbtKey<>("Locked", NbtKey.Type.BOOLEAN);

    public DrawerPanelItem(Settings settings) {
        super(settings);
    }

    public @Nullable SlottedStorage<ItemVariant> getStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side) {
        if (TransferState.NO_BLANK_DRAWERS.get() && stack.get(VARIANT).isBlank()) return null;

        return new Storage(stack, blockEntity, side);
    }

    @Override
    public List<Button> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        return List.of(STORAGE_BUTTON);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        tryOpenConfigScreen(world, user, hand);
        return super.use(world, user, hand);
    }

    @Override
    public ItemVariant currentFilter(ItemStack stack) {
        return stack.get(VARIANT);
    }

    @Override
    public boolean canSetFilter(ItemStack stack, ItemVariant to) {
        if (stack.get(VARIANT).equals(to)) return true;

        return stack.get(COUNT).signum() == 0;
    }

    @Override
    public void setFilter(ItemStack stack, ItemVariant newFilter) {
        stack.put(VARIANT, newFilter);
        stack.put(LOCKED, true);
    }

    @Override
    public boolean locked(ItemStack stack) {
        return stack.get(LOCKED);
    }

    @Override
    public void setLocked(ItemStack stack, boolean locked) {
        stack.put(LOCKED, locked);
    }

    @Override
    public ItemVariant displayedVariant(ItemStack stack) {
        return stack.get(VARIANT);
    }

    @Override
    public BigInteger displayedCount(ItemStack stack) {
        return stack.get(COUNT);
    }


    public static BigInteger getCapacity(ItemStack stack) {
        return new BigInteger(CHOWL_CONFIG.base_panel_capacity()).multiply(BigIntUtils.pow(BigInteger.valueOf(2), stack.get(CAPACITY)));
    }

    @SuppressWarnings("UnstableApiUsage")
    private static class Storage extends PanelStorage implements SingleSlotStorage<ItemVariant> {
        public Storage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side) {
            super(stack, blockEntity, side);
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            var contained = stack.get(VARIANT);

            if (contained.isBlank()) contained = resource;
            if (!contained.equals(resource)) return 0;

            var currentCount = stack.get(COUNT);
            var capacity = DrawerPanelItem.getCapacity(stack);
            var full = currentCount.compareTo(capacity) >= 0;
            long inserted = full ? 0 : Math.min(BigIntUtils.longValueSaturating(capacity.subtract(currentCount)), maxAmount);
            var newCount = full ? capacity : currentCount.add(BigInteger.valueOf(maxAmount));

            updateSnapshots(transaction);
            stack.put(VARIANT, contained);
            stack.put(COUNT, newCount);
            return inserted;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext tx) {
            var contained = stack.get(VARIANT);

            if (contained.isBlank()) return 0;
            if (!contained.equals(resource)) return 0;

            var currentCount = stack.get(COUNT);

            long removed = Math.min(BigIntUtils.longValueSaturating(currentCount), maxAmount);
            var newCount = currentCount.subtract(BigInteger.valueOf(removed));

            updateSnapshots(tx);
            stack.put(COUNT, newCount);

            if (newCount.equals(BigInteger.ZERO) && !stack.get(LOCKED)) {
                stack.put(VARIANT, ItemVariant.blank());
            }

            return removed;
        }

        @Override
        public boolean isResourceBlank() {
            return stack.get(VARIANT).isBlank();
        }

        @Override
        public ItemVariant getResource() {
            return stack.get(VARIANT);
        }

        @Override
        public long getAmount() {
            return BigIntUtils.longValueSaturating(stack.get(COUNT));
        }

        //todo make getamount return lower value so that getcapacity will allow you to insert (for when theres more then an entire long inside panel)
        @Override
        public long getCapacity() {
            return Long.MAX_VALUE;
        }
    }
}