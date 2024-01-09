package com.chyzman.chowl.item;

import com.chyzman.chowl.item.component.CapacityLimitedPanelItem;
import com.chyzman.chowl.item.component.DisplayingPanelItem;
import com.chyzman.chowl.item.component.UpgradeablePanelItem;
import com.chyzman.chowl.transfer.BigStorageView;
import com.chyzman.chowl.transfer.FakeStorageView;
import com.chyzman.chowl.transfer.PanelStorageContext;
import com.chyzman.chowl.util.CompressionManager;
import io.wispforest.owo.ui.base.BaseOwoTooltipComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.chyzman.chowl.util.FormatUtil.formatCount;

@SuppressWarnings("UnstableApiUsage")
public class PanelTooltipComponent extends BaseOwoTooltipComponent<FlowLayout> {
    public PanelTooltipComponent(ItemStack stack) {
        super(() -> {
            var flow = Containers.verticalFlow(Sizing.content(), Sizing.content());
            if (stack.getItem() instanceof DisplayingPanelItem panel) {
                var storage = panel.getStorage(PanelStorageContext.forRendering(stack));
                if (storage != null && !storage.getSlots().isEmpty() && !storage.getSlot(0).isResourceBlank()) {
                    var filterFlow = Containers.verticalFlow(Sizing.content(), Sizing.content());
                    filterFlow.margins(Insets.bottom(2));
                    filterFlow.child(Components.label(Text.translatable("ui.chowl-industries.panel.tooltip.contained.label")));
                    List<StorageView<ItemVariant>> slots = new ArrayList<>(storage.getSlots());
                    slots.removeIf(x -> x instanceof FakeStorageView);
                    for (StorageView<ItemVariant> slot : slots) {
                        var item = slot.getResource();
                        var currentFilter = Containers.horizontalFlow(Sizing.content(), Sizing.content());
                        currentFilter.child(Components.item(item.toStack())
                                .margins(Insets.right(2))
                                .sizing(Sizing.fixed(MinecraftClient.getInstance().textRenderer.fontHeight)));
                        BigInteger count = BigStorageView.bigAmount(slot);
                        if (count.compareTo(BigInteger.ZERO) > 0) {
                            StringBuilder countText = new StringBuilder();
                            countText.append(formatCount(count));
                            if (panel instanceof CapacityLimitedPanelItem cap && cap.capacity(stack).signum() > 0) {
                                countText.append("/").append(formatCount(BigStorageView.bigCapacity(slot)));
                            }
                            currentFilter.child(Components.label(Text.of(countText.toString())));
                        }
                        filterFlow.child(currentFilter);
                    }
                    flow.child(filterFlow);
                }
            }
            if (stack.getItem() instanceof UpgradeablePanelItem panel) {
                var upgrades = panel.upgrades(stack).stream().filter(stack1 -> !stack1.isEmpty()).toList();
                if (!upgrades.isEmpty()) {
                    var upgradesFlow = Containers.verticalFlow(Sizing.content(), Sizing.content());
                    upgradesFlow.margins(Insets.bottom(2));
                    upgradesFlow.child(Components.label(Text.translatable("ui.chowl-industries.panel.tooltip.filter.upgrades.label")));
                    var upgradesBox = Containers.horizontalFlow(Sizing.content(), Sizing.content());
                    upgrades.forEach(upgrade -> upgradesBox.child(Components.item(upgrade).sizing(Sizing.fixed(MinecraftClient.getInstance().textRenderer.fontHeight))));
                    upgradesBox.verticalAlignment(VerticalAlignment.CENTER);
                    upgradesFlow.child(upgradesBox);
                    flow.child(upgradesFlow);
                }
            }
            return flow;
        });
    }
}