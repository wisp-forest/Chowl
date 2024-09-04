package com.chyzman.chowl.industries.screen;

import com.chyzman.chowl.industries.item.component.*;
import com.chyzman.chowl.industries.registry.ChowlComponents;
import com.chyzman.chowl.industries.registry.ServerBoundPackets;
import io.wispforest.owo.client.screens.ScreenUtils;
import io.wispforest.owo.client.screens.SlotGenerator;
import io.wispforest.owo.client.screens.SyncedProperty;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PanelConfigScreenHandler extends ScreenHandler {

    final SyncedProperty<ItemStack> stack;
    public PlayerInventory playerInventory;
    public UpgradesInventory upgradesInventory;

    public static final ExtendedScreenHandlerType<PanelConfigScreenHandler, ItemStack> TYPE = new ExtendedScreenHandlerType<>(PanelConfigScreenHandler::new, ItemStack.OPTIONAL_PACKET_CODEC);

    public PanelConfigScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack stack, @Nullable Consumer<ItemStack> updater) {
        super(TYPE, syncId);
        this.playerInventory = playerInventory;
        this.upgradesInventory = new UpgradesInventory();
        this.stack = this.createProperty(ItemStack.class, stack);

        ServerBoundPackets.addEndecs(endecBuilder());

        this.addServerboundMessage(ConfigFilter.class, (message) -> {
            if (!(this.stack.get().getItem() instanceof FilteringPanelItem filteringPanel)) return;

            var filterVariant = ItemVariant.of(message.newFilter());

            if (!filteringPanel.canSetFilter(this.stack.get(), filterVariant)) return;

            filteringPanel.setFilter(this.stack.get(), filterVariant);

            this.stack.markDirty();
        });

        this.addServerboundMessage(ConfigConfig.class, (message) -> {
            var temp = this.stack.get();

            if (temp.getItem() instanceof DisplayingPanelItem) {
                temp.set(ChowlComponents.DISPLAYING_CONFIG, message.displayConfig);
            }
            if (temp.getItem() instanceof LockablePanelItem lockable) {
                lockable.setLocked(temp, message.locked);
            }

            this.stack.set(temp);
            this.stack.markDirty();
        });

        var generator = SlotGenerator.begin(this::addSlot, 8, 84);

        if (stack.getItem() instanceof UpgradeablePanelItem upgradeable) {
            generator.slotFactory(UpgradeSlot::new);
            generator.grid(upgradesInventory, 0, 8, 1);
            generator.defaultSlotFactory();
        }

        generator.slotFactory((inv, index, x, y) -> new InteractionValidatingSlot(inv, index, x, y,stack1 -> !stack1.equals(stack)));
        generator.playerInventory(playerInventory);

        if (updater != null) {
            this.stack.observe(updater);
        }
    }

    public PanelConfigScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack stack) {
        this(syncId, playerInventory, stack, null);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        //TODO make it so quick moving into upgrade slots will only ever move one item
        return ScreenUtils.handleSlotTransfer(this, slot, 8);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public record ConfigFilter(ItemStack newFilter) {}

    public record ConfigConfig(DisplayingPanelConfig displayConfig, boolean locked) {}
}
