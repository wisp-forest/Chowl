package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.item.component.*;
import com.chyzman.chowl.transfer.PanelStorage;
import com.chyzman.chowl.transfer.TransferState;
import com.chyzman.chowl.util.BigIntUtils;
import com.chyzman.chowl.util.NbtKeyTypes;
import io.wispforest.owo.nbt.NbtKey;
import io.wispforest.owo.ops.ItemOps;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.chyzman.chowl.Chowl.*;

@SuppressWarnings("UnstableApiUsage")
public class DrawerPanelItem extends BasePanelItem implements PanelItem, FilteringPanelItem, LockablePanelItem, DisplayingPanelItem, UpgradeablePanelItem {
    public static final NbtKey<ItemVariant> VARIANT = new NbtKey<>("Variant", NbtKeyTypes.ITEM_VARIANT);
    public static final NbtKey<BigInteger> COUNT = new NbtKey<>("Count", NbtKeyTypes.BIG_INTEGER);
    public static final NbtKey<BigInteger> CAPACITY = new NbtKey<>("Capacity", NbtKeyTypes.BIG_INTEGER);
    public static final NbtKey<Boolean> LOCKED = new NbtKey<>("Locked", NbtKey.Type.BOOLEAN);
    public static final NbtKey.ListKey<ItemStack> UPGRADES_LIST = new NbtKey.ListKey<>("Upgrades", NbtKey.Type.ITEM_STACK);

    public DrawerPanelItem(Settings settings) {
        super(settings);
    }

    public @Nullable SlottedStorage<ItemVariant> getStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side) {
        if (TransferState.NO_BLANK_DRAWERS.get() && stack.get(VARIANT).isBlank()) return null;

        return new Storage(stack, blockEntity, side);
    }

    @Override
    public List<Button> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        var returned = new ArrayList<Button>();
        returned.add(STORAGE_BUTTON);
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            returned.add(new Button(i * 2, 0, (i + 1) * 2, 2,
                    (world, frame, useSide, useStack, player, hand) -> {
                        var stackInHand = player.getStackInHand(hand);
                        if (stackInHand.isEmpty()) return ActionResult.PASS;
                        if (!(useStack.getItem() instanceof PanelItem)) return ActionResult.PASS;

                        var upgrades = upgrades(useStack);

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
                    },
                    (world, attackedDrawerFrame, attackedSide, attackedStack, player) -> {
                        var upgrades = upgrades(attackedStack);

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
                    },
                    null,
                    (client, entity, hitResult, vertexConsumers, matrices, light, overlay, hovered) -> {
                        var upgrades = upgrades(stack);
                        if (upgrades.get(finalI).isEmpty()) return;
                        matrices.scale(1, 1, 1 / 8f);
                        client.getItemRenderer().renderItem(upgrades.get(finalI), ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, overlay, client.getItemRenderer().getModels().getModel(upgrades.get(finalI)));
                        matrices.scale(1, 1, 8);
                    }));
        }
        return returned;
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
        return new BigInteger(CHOWL_CONFIG.base_panel_capacity()).multiply(POWER_CACHE.getUnchecked(stack.get(CAPACITY).min(BigInteger.valueOf(100000000))));
    }

    @Override
    public List<ItemStack> upgrades(ItemStack stack) {
        var returned = new ArrayList<ItemStack>();
        stack.get(UPGRADES_LIST).forEach(nbtElement -> returned.add(ItemStack.fromNbt((NbtCompound) nbtElement)));
        while (returned.size() < 8) returned.add(ItemStack.EMPTY);
        return returned;
    }

    @Override
    public void setUpgrades(ItemStack stack, List<ItemStack> upgrades) {
        var nbtList = new NbtList();
        upgrades.forEach(itemStack -> nbtList.add(itemStack.writeNbt(new NbtCompound())));
        stack.put(UPGRADES_LIST, nbtList);
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
            var newCount = full ? capacity : currentCount.add(BigInteger.valueOf(maxAmount));

            updateSnapshots(transaction);
            stack.put(VARIANT, contained);
            stack.put(COUNT, newCount);

            ItemVariant finalContained = contained;
            var voiding = ((DrawerPanelItem) stack.getItem()).hasUpgrade(stack, upgrade -> upgrade.isIn(VOID_UPGRADE_TAG) || (!finalContained.getItem().isFireproof() && upgrade.isIn(LAVA_UPGRADE_TAG)));
            if (voiding) return maxAmount;
            return full ? 0 : Math.min(BigIntUtils.longValueSaturating(capacity.subtract(currentCount)), maxAmount);
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

            if (newCount.compareTo(BigInteger.ZERO) <= 0) {
                if (!stack.get(LOCKED)) {
                    stack.put(VARIANT, ItemVariant.blank());
                }

                //TODO: make this only happen when empty
                if (stack.getItem() instanceof UpgradeablePanelItem panelItem) {
                    if (panelItem.hasUpgrade(stack, upgrade -> upgrade.isIn(EXPLOSIVE_UPGRADE_TAG))) {
                        var world = blockEntity.getWorld();
                        var pos = blockEntity.getPos();
                        var upgrades = panelItem.upgrades(stack);
                        AtomicInteger power = new AtomicInteger();
                        upgrades.forEach(upgrade -> {
                                    if (upgrade.isIn(EXPLOSIVE_UPGRADE_TAG)) {
                                        power.addAndGet(1);
                                        upgrade.decrement(1);
                                        panelItem.setUpgrades(stack, upgrades);
                                        world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), power.get() + 1, false, World.ExplosionSourceType.BLOCK);
                                    }
                                }
                        );
                    }
                }
            }
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