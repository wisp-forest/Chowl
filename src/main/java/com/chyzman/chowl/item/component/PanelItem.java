package com.chyzman.chowl.item.component;

import com.chyzman.chowl.block.BlockButtonProvider;
import com.chyzman.chowl.block.DrawerFrameBlock;
import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.item.renderer.button.ButtonRenderer;
import com.chyzman.chowl.screen.PanelConfigSreenHandler;
import com.chyzman.chowl.transfer.TransferState;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public interface PanelItem {
    Button STORAGE_BUTTON = new ButtonBuilder(2, 2, 14, 14)
            .onUse((world, frame, side, stack, player, hand) -> {
                var stackInHand = player.getStackInHand(hand);
                if (stackInHand.isEmpty()) return ActionResult.PASS;
                if (!(stack.getItem() instanceof PanelItem)) return ActionResult.PASS;

                PanelItem panel = (PanelItem) stack.getItem();

                if (world.isClient) return ActionResult.SUCCESS;

                var storage = panel.getStorage(stack, frame, side);

                try (var tx = Transaction.openOuter()) {
                    StorageUtil.move(
                            PlayerInventoryStorage.of(player).getHandSlot(hand),
                            storage,
                            variant -> true,
                            stackInHand.getCount(),
                            tx
                    );

                    tx.commit();
                }

                return ActionResult.SUCCESS;
            })
            .onAttack((world, drawerFrame, side, stack, player) -> {
                PanelItem panel = (PanelItem) stack.getItem();

                if (panel.canExtractFromButton()) {
                    var storage = panel.getStorage(stack, drawerFrame, side);

                    if (storage == null) return ActionResult.FAIL;
                    if (world.isClient) return ActionResult.SUCCESS;

                    try (var tx = Transaction.openOuter()) {
                        var resource = StorageUtil.findExtractableResource(storage, tx);

                        if (resource != null) {
                            var extracted = storage.extract(resource, player.isSneaking() ? resource.toStack().getMaxCount() : 1, tx);

                            if (extracted > 0) {
                                PlayerInventoryStorage.of(player).offerOrDrop(resource, extracted, tx);
                                tx.commit();
                                return ActionResult.SUCCESS;
                            }
                        }
                    }
                }

                player.getInventory().offerOrDrop(stack);
                drawerFrame.stacks.set(side.getId(), new Pair<>(ItemStack.EMPTY, 0));
                drawerFrame.markDirty();
                return ActionResult.SUCCESS;
            })
            .onDoubleClick((world, frame, side, stack, player) -> {
                try {
                    TransferState.NO_BLANK_DRAWERS.set(true);

                    var panel = (PanelItem) stack.getItem();
                    var storage = panel.getStorage(stack, frame, side);

                    if (storage == null) return ActionResult.FAIL;
                    if (world.isClient) return ActionResult.SUCCESS;

                    try (var tx = Transaction.openOuter()) {
                        StorageUtil.move(PlayerInventoryStorage.of(player), storage, variant -> true, Long.MAX_VALUE, tx);

                        tx.commit();

                        return ActionResult.SUCCESS;
                    }
                } finally {
                    TransferState.NO_BLANK_DRAWERS.set(false);
                }
            }).build();

    @SuppressWarnings("UnstableApiUsage")
    @Nullable SlottedStorage<ItemVariant> getStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side);

    default List<Button> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        return Collections.emptyList();
    }

    default boolean canExtractFromButton() {
        return true;
    }

    default boolean hasConfig() {
        return false;
    }

    default void openConfig(ItemStack stack, PlayerEntity user, @Nullable Consumer<ItemStack> updater) {
        var factory = new ExtendedScreenHandlerFactory() {
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new PanelConfigSreenHandler(syncId, playerInventory, stack, updater);
            }

            @Override
            public Text getDisplayName() {
                return Text.translatable("container.chowl.panel_config.title");
            }

            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                buf.writeItemStack(stack);
            }
        };

        user.openHandledScreen(factory);
    }

    record Button(
            float minX, float minY,
            float maxX, float maxY,
            UseFunction use,
            AttackFunction attack,
            DoubleClickFunction doubleClick,
            BlockButtonProvider.RenderConsumer render) {
        public BlockButtonProvider.Button toBlockButton() {
            return new BlockButtonProvider.Button(
                    minX, minY, maxX, maxY,
                    use != null ? (state, world, pos, player, hand, hit) -> {
                        if (!(world.getBlockEntity(pos) instanceof DrawerFrameBlockEntity drawerFrame))
                            return ActionResult.PASS;
                        var side = DrawerFrameBlock.getSide(hit);

                        return use.onUse(world, drawerFrame, side, drawerFrame.stacks.get(side.getId()).getLeft(), player, hand);
                    } : null,
                    attack != null ? (world, state, hit, player) -> {
                        var pos = hit.getBlockPos();
                        var side = DrawerFrameBlock.getSide(hit);

                        if (!(world.getBlockEntity(pos) instanceof DrawerFrameBlockEntity drawerFrame))
                            return ActionResult.PASS;

                        return attack.onAttack(world, drawerFrame, side, drawerFrame.stacks.get(side.getId()).getLeft(), player);
                    } : null,
                    doubleClick != null ? (world, state, hit, player) -> {
                        var pos = hit.getBlockPos();
                        var side = DrawerFrameBlock.getSide(hit);

                        if (!(world.getBlockEntity(pos) instanceof DrawerFrameBlockEntity drawerFrame))
                            return ActionResult.PASS;

                        return doubleClick.onDoubleClick(world, drawerFrame, side, drawerFrame.stacks.get(side.getId()).getLeft(), player);
                    } : null,
                    render
            );
        }
    }

    class ButtonBuilder {
        private Vector2f min = new Vector2f();
        private Vector2f max = new Vector2f();

        private UseFunction use;
        private AttackFunction attack;
        private DoubleClickFunction doubleClick;
        private BlockButtonProvider.RenderConsumer render;

        public ButtonBuilder(float minX, float minY, float maxX, float maxY) {
            this.min.x = minX;
            this.min.y = minY;
            this.max.x = maxX;
            this.max.y = maxY;
        }

        public ButtonBuilder onUse(UseFunction use) {
            this.use = use;
            return this;
        }

        public ButtonBuilder onAttack(AttackFunction attack) {
            this.attack = attack;
            return this;
        }

        public ButtonBuilder onDoubleClick(DoubleClickFunction doubleClick) {
            this.doubleClick = doubleClick;
            return this;
        }

        public ButtonBuilder onRenderer(BlockButtonProvider.RenderConsumer render) {
            this.render = render;
            return this;
        }

        public Button build() {
            return new Button(min.x, min.y, max.x, max.y, use, attack, doubleClick, render);
        }
    }

    @FunctionalInterface
    interface UseFunction {
        ActionResult onUse(World world, DrawerFrameBlockEntity frame, Direction side, ItemStack stack, PlayerEntity player, Hand hand);
    }

    @FunctionalInterface
    interface AttackFunction {
        ActionResult onAttack(World world, DrawerFrameBlockEntity frame, Direction side, ItemStack stack, PlayerEntity player);
    }

    @FunctionalInterface
    interface DoubleClickFunction {
        ActionResult onDoubleClick(World world, DrawerFrameBlockEntity frame, Direction side, ItemStack stack, PlayerEntity player);
    }
}