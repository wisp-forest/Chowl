package com.chyzman.chowl.industries.screen;

import com.chyzman.chowl.industries.item.component.LockablePanelItem;
import com.chyzman.chowl.industries.item.component.UpgradeablePanelItem;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static com.chyzman.chowl.industries.Chowl.id;

public class PanelConfigScreen extends BaseOwoHandledScreen<FlowLayout, PanelConfigScreenHandler> {

    public PanelConfigScreen(PanelConfigScreenHandler screenHandler, PlayerInventory inventory, Text title) {
        super(screenHandler, inventory, title);
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        ItemStack stack = handler.stack.get();

        var configFlow = Containers.stack(Sizing.fixed(160), Sizing.fixed(160))
                .<StackLayout>configure(layout -> {
                    layout.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
                    layout.child(Components.item(stack.getItem().getDefaultStack()).sizing(Sizing.fixed(120)));

                    var upperRightFlow = Containers.horizontalFlow(Sizing.content(), Sizing.fixed(20));
                    upperRightFlow.child(Components.texture(id("textures/item/cog.png"), 0, 0, 16, 16, 16, 16).sizing(Sizing.fixed(20)));
                    upperRightFlow.child(Components.texture(id("textures/item/remove.png"), 0, 0, 16, 16, 16, 16).sizing(Sizing.fixed(20)));
                    layout.child(upperRightFlow);
                    upperRightFlow.positioning(Positioning.relative(100, 0));


                    if (stack.getItem() instanceof LockablePanelItem) {
                        var locked = ((LockablePanelItem) stack.getItem()).locked(stack);
                        layout.child(Components.label(Text.literal(locked ? "Locked" : "Unlocked")).positioning(Positioning.relative(0, 0)));
                    }


                    if (stack.getItem() instanceof UpgradeablePanelItem) {
                        var upgradesFlow = Containers.horizontalFlow(Sizing.fill(), Sizing.fixed(20));
                        upgradesFlow.surface((context, component) -> {
                            RenderSystem.enableBlend();
                            Surface.tiled(id("textures/gui/container/upgrades.png"), 160, 20).draw(context, component);
                        });
                        for (int i = 0; i < 8; i++) {
                            var slot = this.slotAsComponent(i).margins(Insets.of(2));
                            upgradesFlow.child(slot);
                        }

                        layout.child(upgradesFlow);
                        upgradesFlow.positioning(Positioning.relative(0, 100));
                    }

                    layout.surface(Surface.tiled(id("textures/block/frame.png"), 160, 160));
                });

        var inventoryFlow = Containers.grid(Sizing.content(), Sizing.content(), 4, 9)
                .<GridLayout>configure(gridLayout -> {
                    gridLayout.margins(Insets.top(7));
                    for (int i = 0; i < handler.playerInventory.main.size(); i++) {
                        var slot = this.slotAsComponent(i + 8);
                        slot.margins(Insets.of(1));
                        if (i > 26) slot.margins(Insets.of(1).withTop(5));
                        gridLayout.child(slot, i / 9, i % 9);
                        gridLayout.surface(Surface.tiled(id("textures/gui/container/inventory.png"), 162, 76));
                    }
                });

        var verticalFlow = (FlowLayout) Containers.verticalFlow(Sizing.content(), Sizing.content())
                .<FlowLayout>configure(flowLayout -> {
                    flowLayout.surface(Surface.PANEL);
                    flowLayout.padding(Insets.of(7));
                    flowLayout.horizontalAlignment(HorizontalAlignment.CENTER);

                    flowLayout.child(
                            Containers.stack(Sizing.content(4),Sizing.content(4))
                                    .child(configFlow)
                                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                                    .surface(Surface.PANEL_INSET));

                    flowLayout.child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                            .child(inventoryFlow)
                            .horizontalAlignment(HorizontalAlignment.CENTER));
                });

        rootComponent.child(Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100))
                .child(verticalFlow)
                .surface(Surface.VANILLA_TRANSLUCENT)
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER)
        );
    }

//    private void resendConfig() {
//        if (ignoreChanges) return;
//
//        var displayConfig = new DisplayingPanelConfig.Builder();
//        boolean locked = false;
//
//        if (lockedCheckbox != null) locked = lockedCheckbox.checked();
//        if (showCountCheckBox != null) displayConfig.hideCount(!showCountCheckBox.checked());
//        if (showCapacityCheckBox != null) displayConfig.hideCapacity(!showCapacityCheckBox.checked());
//        if (showItemCheckBox != null) displayConfig.hideItem(!showItemCheckBox.checked());
//        if (showNameCheckBox != null) displayConfig.hideName(!showNameCheckBox.checked());
//        if (showPercentageCheckBox != null) displayConfig.showPercentage(showPercentageCheckBox.checked());
//        if (hideUpgradesCheckBox != null) displayConfig.hideUpgrades(hideUpgradesCheckBox.checked());
//        if (hideButtonsCheckBox != null) displayConfig.hideButtons(hideButtonsCheckBox.checked());
//        if (ignoreTemplatingCheckBox != null) displayConfig.ignoreTemplating(ignoreTemplatingCheckBox.checked());
//
//
//        handler.sendMessage(new PanelConfigScreenHandler.ConfigConfig(displayConfig.build(), locked));
//    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        var filter = this.component(ItemComponent.class, "filter-slot");
        if (filter != null && filter.isInBoundingBox(mouseX, mouseY)) return false;

        return super.isClickOutsideBounds(mouseX, mouseY, left, top, button);
    }

    @Override
    public void removed() {
    }

    public class FakeSlotComponent extends ItemComponent {

        protected FakeSlotComponent(ItemStack stack) {
            super(stack);
        }

        @Override
        public boolean shouldDrawTooltip(double mouseX, double mouseY) {
            return handler.getCursorStack().isEmpty() && super.shouldDrawTooltip(mouseX, mouseY);
        }
    }
}
