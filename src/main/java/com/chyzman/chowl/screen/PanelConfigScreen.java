package com.chyzman.chowl.screen;

import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class PanelConfigScreen extends BaseOwoHandledScreen<FlowLayout, PanelConfigSreenHandler> {

    public PanelConfigScreen(PanelConfigSreenHandler screenHandler, PlayerInventory inventory, Text title) {
        super(screenHandler, inventory, title);
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var verticalFlow = (FlowLayout) Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        rootComponent.child(Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100))
                .child(Components.label(Text.translatable("ui.chowl-industries.config_panel.title")))
                .child(Containers.verticalScroll(Sizing.fill(50), Sizing.fill(75), verticalFlow)
                        .scrollbar(ScrollContainer.Scrollbar.vanilla())
                        .scrollbarThiccness(10)
                        .surface(Surface.PANEL)
                        .padding(Insets.of(4))
                ).surface(Surface.VANILLA_TRANSLUCENT)
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER)
        );
    }


    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void removed() {
    }
}