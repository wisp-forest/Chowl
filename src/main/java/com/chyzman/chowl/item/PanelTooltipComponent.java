package com.chyzman.chowl.item;

import com.chyzman.chowl.item.component.DisplayingPanelItem;
import com.chyzman.chowl.item.component.FilteringPanelItem;
import io.wispforest.owo.ui.base.BaseOwoTooltipComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.math.BigInteger;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class PanelTooltipComponent extends BaseOwoTooltipComponent<FlowLayout> {
    public PanelTooltipComponent(ItemStack stack) {
        super(() -> {
            var flow = Containers.verticalFlow(Sizing.content(), Sizing.content());
            if (stack.getItem() instanceof DisplayingPanelItem panel) {
                var currentFilter = panel.displayedVariant(stack);
                if (!currentFilter.isBlank()) {
                    var filterFlow = Containers.horizontalFlow(Sizing.content(), Sizing.content());
                    filterFlow.child(Components.label(Text.translatable("ui.chowl-industries.panel.tooltip.filter.label")));
                    filterFlow.child(Components.item(currentFilter.toStack()));
                    filterFlow.verticalAlignment(VerticalAlignment.CENTER);
                    flow.child(filterFlow);
                }
                var currentCount = panel.displayedCount(stack);
                if (currentCount.compareTo(BigInteger.ZERO) > 0) {
                    var filterFlow = Containers.horizontalFlow(Sizing.content(), Sizing.fixed(18));
                    filterFlow.child(Components.label(Text.translatable("ui.chowl-industries.panel.tooltip.count.label", currentCount.toString())));
                    filterFlow.verticalAlignment(VerticalAlignment.CENTER);
                    flow.child(filterFlow);
                }
            }
            return flow;
        });
    }
}