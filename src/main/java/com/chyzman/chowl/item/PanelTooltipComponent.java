package com.chyzman.chowl.item;

import com.chyzman.chowl.item.component.DisplayingPanelItem;
import com.chyzman.chowl.item.component.UpgradeablePanelItem;
import io.wispforest.owo.ui.base.BaseOwoTooltipComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.math.BigInteger;

@SuppressWarnings("UnstableApiUsage")
public class PanelTooltipComponent extends BaseOwoTooltipComponent<FlowLayout> {
    public PanelTooltipComponent(ItemStack stack) {
        super(() -> {
            var flow = Containers.verticalFlow(Sizing.content(), Sizing.content());
            if (stack.getItem() instanceof DisplayingPanelItem panel) {
                var currentFilter = panel.displayedVariant(stack);
                if (!currentFilter.isBlank()) {
                    var currentFlow = Containers.horizontalFlow(Sizing.content(), Sizing.content());
                    currentFlow.child(Components.label(Text.translatable("ui.chowl-industries.panel.tooltip.filter.label")));
                    currentFlow.child(Components.item(currentFilter.toStack()).sizing(Sizing.fixed(MinecraftClient.getInstance().textRenderer.fontHeight)));
                    currentFlow.verticalAlignment(VerticalAlignment.CENTER);
                    flow.child(currentFlow);
                }
                var currentCount = panel.displayedCount(stack);
                if (currentCount.compareTo(BigInteger.ZERO) > 0) {
                    var currentFlow = Containers.horizontalFlow(Sizing.content(), Sizing.content());
                    currentFlow.child(Components.label(Text.translatable("ui.chowl-industries.panel.tooltip.count.label", currentCount.toString())));
                    currentFlow.verticalAlignment(VerticalAlignment.CENTER);
                    flow.child(currentFlow);
                }
            }
            if (stack.getItem() instanceof DrawerPanelItem) {
                var currentCapacity = DrawerPanelItem.getCapacity(stack);
                if (currentCapacity.compareTo(BigInteger.ZERO) > 0) {
                    var currentFlow = Containers.horizontalFlow(Sizing.content(), Sizing.content());
                    currentFlow.child(Components.label(Text.translatable("ui.chowl-industries.panel.tooltip.capacity.label", currentCapacity.toString().substring(0, Math.min(1000, currentCapacity.toString().length())))));
                    currentFlow.verticalAlignment(VerticalAlignment.CENTER);
                    flow.child(currentFlow);
                }
            }
            if (stack.getItem() instanceof UpgradeablePanelItem panel) {
                var upgrades = panel.upgrades(stack).stream().filter(stack1 -> !stack1.isEmpty()).toList();
                if (!upgrades.isEmpty()) {
                    var currentFlow = Containers.horizontalFlow(Sizing.content(), Sizing.content());
                    currentFlow.child(Components.label(Text.translatable("ui.chowl-industries.panel.tooltip.filter.upgrades")));
                    upgrades.forEach(upgrade -> currentFlow.child(Components.item(upgrade).sizing(Sizing.fixed(MinecraftClient.getInstance().textRenderer.fontHeight))));
                    currentFlow.verticalAlignment(VerticalAlignment.CENTER);
                    flow.child(currentFlow);
                }
            }
            return flow;
        });
    }
}