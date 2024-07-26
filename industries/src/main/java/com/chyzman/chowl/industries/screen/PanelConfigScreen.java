package com.chyzman.chowl.industries.screen;

import com.chyzman.chowl.industries.client.AutoGridLayout;
import com.chyzman.chowl.industries.client.DisableableCheckboxComponent;
import com.chyzman.chowl.industries.item.component.*;
import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.component.SmallCheckboxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static com.chyzman.chowl.industries.Chowl.id;

public class PanelConfigScreen extends BaseOwoHandledScreen<FlowLayout, PanelConfigScreenHandler> {
    private FakeSlotComponent filterSlot;
    private DisableableCheckboxComponent lockedCheckbox;
    private SmallCheckboxComponent showCountCheckBox;
    private SmallCheckboxComponent showCapacityCheckBox;
    private SmallCheckboxComponent showItemCheckBox;
    private SmallCheckboxComponent showNameCheckBox;
    private SmallCheckboxComponent showPercentageCheckBox;
    private SmallCheckboxComponent hideUpgradesCheckBox;
    private SmallCheckboxComponent hideButtonsCheckBox;
    private SmallCheckboxComponent ignoreTemplatingCheckBox;

    private boolean ignoreChanges = false;

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

        if (stack.getItem() instanceof FilteringPanelItem filtering) {
            this.filterSlot = new FakeSlotComponent(filtering.currentFilter(stack).toStack());

            filterSlot.mouseDown().subscribe((mouseX, mouseY, button) -> {
                if (button == 0) {
                    handler.sendMessage(new PanelConfigScreenHandler.ConfigFilter(handler.getCursorStack()));
                }
                return true;
            });
            filterSlot.setTooltipFromStack(true);
            filterSlot.id("filter-slot");

            if (stack.getItem() instanceof LockablePanelItem lockable) {
                this.lockedCheckbox = new DisableableCheckboxComponent(Text.translatable("ui.chowl-industries.config_panel.locked"));

                lockedCheckbox.checked(lockable.locked(stack));
                lockedCheckbox.onChanged().subscribe(nowChecked -> resendConfig());
                lockedCheckbox.disabled(filtering.currentFilter(stack).isBlank());
            }
        }

        if (stack.getItem() instanceof DisplayingPanelItem displaying) {
            var config = DisplayingPanelItem.getConfig(stack);

            this.showCountCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.show_count"));
            showCountCheckBox.checked(!config.hideCount());
            showCountCheckBox.onChanged().subscribe(nowChecked -> resendConfig());

            this.showCapacityCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.show_capacity"));
            showCapacityCheckBox.checked(!config.hideCapacity());
            showCapacityCheckBox.onChanged().subscribe(nowChecked -> resendConfig());

            if (displaying.supportsHideItem()) {
                this.showItemCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.show_item"));
                showItemCheckBox.checked(!config.hideItem());
                showItemCheckBox.onChanged().subscribe(nowChecked -> resendConfig());
            }

            if (displaying.supportsHideName()) {
                this.showNameCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.show_name"));
                showNameCheckBox.checked(!config.hideName());
                showNameCheckBox.onChanged().subscribe(nowChecked -> resendConfig());
            }

            this.showPercentageCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.show_percentage"));
            showPercentageCheckBox.checked(config.showPercentage());
            showPercentageCheckBox.onChanged().subscribe(nowChecked -> resendConfig());

            if (stack.getItem() instanceof UpgradeablePanelItem) {
                this.hideUpgradesCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.hide_upgrades"));
                hideUpgradesCheckBox.checked(config.hideUpgrades());
                hideUpgradesCheckBox.onChanged().subscribe(nowChecked -> resendConfig());
            }

            this.hideButtonsCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.hide_buttons"));
            hideButtonsCheckBox.checked(config.hideButtons());
            hideButtonsCheckBox.onChanged().subscribe(nowChecked -> resendConfig());

            this.ignoreTemplatingCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.ignore_templating"));
            ignoreTemplatingCheckBox.checked(config.ignoreTemplating());
            ignoreTemplatingCheckBox.onChanged().subscribe(nowChecked -> resendConfig());
        }

        var inventoryFlow = Containers.grid(Sizing.content(), Sizing.content(), 4, 9)
                .<GridLayout>configure(gridLayout -> {
                    gridLayout.margins(Insets.top(7));
                    for (int i = 0; i < handler.inventory.main.size(); i++) {
                        var slot = this.slotAsComponent(i);
                        slot.margins(Insets.of(1));
                        if (i > 26) slot.margins(Insets.of(1).withTop(5));
                        gridLayout.child(slot, i / 9, i % 9);
                        gridLayout.surface(Surface.tiled(id("textures/gui/container/inventory.png"), 162, 76));
                    }
                });
        var verticalFlow = (FlowLayout) Containers.verticalFlow(Sizing.fixed(225), Sizing.content())
                .<FlowLayout>configure(flowLayout -> {
                    flowLayout.surface(Surface.PANEL);
                    flowLayout.padding(Insets.of(7));

                    var grid = new AutoGridLayout(Sizing.fill(100), Sizing.content(), 5, 2);
                    flowLayout.child(grid);

                    if (filterSlot != null) {
                        var slotFlow = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18));
                        slotFlow
                                .margins(Insets.bottom(1))
                                .verticalAlignment(VerticalAlignment.CENTER);

                        slotFlow
                                .child(filterSlot
                                        .margins(Insets.of(1).withRight(3)))
                                .child(Components.texture(id("textures/gui/container/slot.png"), 0, 0, 18, 18, 18, 18)
                                        .positioning(Positioning.absolute(0, 0)))
                                .child(Components.label(Text.translatable("ui.chowl-industries.config_panel.filter")));

                        grid.child(slotFlow);
                    }

                    if (lockedCheckbox != null) {
                        grid.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                                .<FlowLayout>configure(flow -> {
                                    flow.margins(Insets.bottom(1));
                                    flow.verticalAlignment(VerticalAlignment.CENTER);
                                })
                                .child(lockedCheckbox));
                    }

                    if (showCountCheckBox != null) {
                        grid.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                                .<FlowLayout>configure(flow -> {
                                    flow.margins(Insets.bottom(1));
                                    flow.verticalAlignment(VerticalAlignment.CENTER);
                                })
                                .child(showCountCheckBox));
                    }

                    if (showCapacityCheckBox != null) {
                        grid.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                                .<FlowLayout>configure(flow -> {
                                    flow.margins(Insets.bottom(1));
                                    flow.verticalAlignment(VerticalAlignment.CENTER);
                                })
                                .child(showCapacityCheckBox));
                    }

                    if (showItemCheckBox != null) {
                        grid.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                                .<FlowLayout>configure(flow -> {
                                    flow.margins(Insets.bottom(1));
                                    flow.verticalAlignment(VerticalAlignment.CENTER);
                                })
                                .child(showItemCheckBox));
                    }

                    if (showNameCheckBox != null) {
                        grid.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                                .<FlowLayout>configure(flow -> {
                                    flow.margins(Insets.bottom(1));
                                    flow.verticalAlignment(VerticalAlignment.CENTER);
                                })
                                .child(showNameCheckBox));
                    }

                    if (showPercentageCheckBox != null) {
                        grid.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                                .<FlowLayout>configure(flow -> {
                                    flow.margins(Insets.bottom(1));
                                    flow.verticalAlignment(VerticalAlignment.CENTER);
                                })
                                .child(showPercentageCheckBox));
                    }

                    if (hideUpgradesCheckBox != null) {
                        grid.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                                .<FlowLayout>configure(flow -> {
                                    flow.margins(Insets.bottom(1));
                                    flow.verticalAlignment(VerticalAlignment.CENTER);
                                })
                                .child(hideUpgradesCheckBox));
                    }

                    if (hideButtonsCheckBox != null) {
                        grid.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                                .<FlowLayout>configure(flow -> {
                                    flow.margins(Insets.bottom(1));
                                    flow.verticalAlignment(VerticalAlignment.CENTER);
                                })
                                .child(hideButtonsCheckBox));
                    }

                    if (ignoreTemplatingCheckBox != null) {
                        grid.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                                .<FlowLayout>configure(flow -> {
                                    flow.margins(Insets.bottom(1));
                                    flow.verticalAlignment(VerticalAlignment.CENTER);
                                })
                                .child(ignoreTemplatingCheckBox));
                    }

                    flowLayout.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.content())
                        .child(inventoryFlow)
                        .horizontalAlignment(HorizontalAlignment.CENTER));
                });


        handler.stack.observe(newStack -> {
            try {
                ignoreChanges = true;

                if (newStack.getItem() instanceof FilteringPanelItem filtering) {
                    filterSlot.stack(filtering.currentFilter(newStack).toStack());

                    if (newStack.getItem() instanceof LockablePanelItem lockable) {
                        lockedCheckbox.checked(lockable.locked(newStack));
                        lockedCheckbox.disabled(filtering.currentFilter(newStack).isBlank());
                    }
                }

                if (stack.getItem() instanceof DisplayingPanelItem) {
                    var newConfig = DisplayingPanelItem.getConfig(newStack);

                    showCountCheckBox.checked(!newConfig.hideCount());
                    showCapacityCheckBox.checked(!newConfig.hideCapacity());
                    showItemCheckBox.checked(!newConfig.hideItem());
                    showNameCheckBox.checked(!newConfig.hideName());
                    showPercentageCheckBox.checked(newConfig.showPercentage());
                    hideUpgradesCheckBox.checked(newConfig.hideUpgrades());
                    hideButtonsCheckBox.checked(newConfig.hideButtons());
                    ignoreTemplatingCheckBox.checked(newConfig.ignoreTemplating());
                }
            } finally {
                ignoreChanges = false;
            }
        });

        rootComponent.child(Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100))
                .child(Components.label(Text.translatable("ui.chowl-industries.config_panel.title")))
                .child(verticalFlow)
                .surface(Surface.VANILLA_TRANSLUCENT)
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER)
        );
    }

    private void resendConfig() {
        if (ignoreChanges) return;

        var displayConfig = new DisplayingPanelConfig.Builder();
        boolean locked = false;

        if (lockedCheckbox != null) locked = lockedCheckbox.checked();
        if (showCountCheckBox != null) displayConfig.hideCount(!showCountCheckBox.checked());
        if (showCapacityCheckBox != null) displayConfig.hideCapacity(!showCapacityCheckBox.checked());
        if (showItemCheckBox != null) displayConfig.hideItem(!showItemCheckBox.checked());
        if (showNameCheckBox != null) displayConfig.hideName(!showNameCheckBox.checked());
        if (showPercentageCheckBox != null) displayConfig.showPercentage(showPercentageCheckBox.checked());
        if (hideUpgradesCheckBox != null) displayConfig.hideUpgrades(hideUpgradesCheckBox.checked());
        if (hideButtonsCheckBox != null) displayConfig.hideButtons(hideButtonsCheckBox.checked());
        if (ignoreTemplatingCheckBox != null) displayConfig.ignoreTemplating(ignoreTemplatingCheckBox.checked());


        handler.sendMessage(new PanelConfigScreenHandler.ConfigConfig(displayConfig.build(), locked));
    }

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