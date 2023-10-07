package com.chyzman.chowl.screen;

import com.chyzman.chowl.item.component.*;
import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

public class PanelConfigScreen extends BaseOwoHandledScreen<FlowLayout, PanelConfigSreenHandler> {
    private FakeSlotComponent filterSlot;
    private SmallCheckboxComponent lockedCheckbox;
    private SmallCheckboxComponent showCountCheckBox;
    private SmallCheckboxComponent showItemCheckBox;
    private SmallCheckboxComponent showNameCheckBox;

    public PanelConfigScreen(PanelConfigSreenHandler screenHandler, PlayerInventory inventory, Text title) {
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
                    handler.sendMessage(new PanelConfigSreenHandler.ConfigFilter(handler.getCursorStack()));
                }
                return true;
            });
            filterSlot.setTooltipFromStack(true);
            filterSlot.id("filter-slot");
        }

        if (stack.getItem() instanceof LockablePanelItem lockable) {
            this.lockedCheckbox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.locked"));

            lockedCheckbox.checked(lockable.locked(stack));
            lockedCheckbox.onChanged().subscribe(nowChecked -> resendConfig());
        }

        if (stack.getItem() instanceof DisplayingPanelItem) {
            var config = stack.get(DisplayingPanelItem.CONFIG);

            this.showCountCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.show_count"));
            showCountCheckBox.checked(!config.hideCount());
            showCountCheckBox.onChanged().subscribe(nowChecked -> resendConfig());

            this.showItemCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.show_item"));
            showItemCheckBox.checked(!config.hideItem());
            showItemCheckBox.onChanged().subscribe(nowChecked -> resendConfig());

            this.showNameCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.show_name"));
            showNameCheckBox.checked(!config.hideName());
            showNameCheckBox.onChanged().subscribe(nowChecked -> resendConfig());
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
        var verticalFlow = (FlowLayout) Containers.verticalFlow(Sizing.fixed(176), Sizing.content())
                .<FlowLayout>configure(flowLayout -> {
                    flowLayout.surface(Surface.PANEL);
                    flowLayout.padding(Insets.of(7));

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

                        flowLayout.child(slotFlow);
                    }

                    if (lockedCheckbox != null) {
                        flowLayout.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                            .<FlowLayout>configure(flow -> {
                                flow.margins(Insets.bottom(1));
                                flow.verticalAlignment(VerticalAlignment.CENTER);
                            })
                            .child(lockedCheckbox));
                    }

                    if (showCountCheckBox != null) {
                        flowLayout.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                            .<FlowLayout>configure(flow -> {
                                flow.margins(Insets.bottom(1));
                                flow.verticalAlignment(VerticalAlignment.CENTER);
                            })
                            .child(showCountCheckBox));
                    }

                    if (showItemCheckBox != null) {
                        flowLayout.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                            .<FlowLayout>configure(flow -> {
                                flow.margins(Insets.bottom(1));
                                flow.verticalAlignment(VerticalAlignment.CENTER);
                            })
                            .child(showItemCheckBox));
                    }

                    if (showNameCheckBox != null) {
                        flowLayout.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                            .<FlowLayout>configure(flow -> {
                                flow.margins(Insets.bottom(1));
                                flow.verticalAlignment(VerticalAlignment.CENTER);
                            })
                            .child(showNameCheckBox));
                    }

                    flowLayout.child(inventoryFlow);
                });


        handler.stack.observe(newStack -> {
            if (newStack.getItem() instanceof FilteringPanelItem filtering) {
                filterSlot.stack(filtering.currentFilter(newStack).toStack());
            }

            if (newStack.getItem() instanceof LockablePanelItem lockable) {
                lockedCheckbox.checked(lockable.locked(newStack));
            }

            if (stack.getItem() instanceof DisplayingPanelItem) {
                var newConfig = newStack.get(DisplayingPanelItem.CONFIG);

                showCountCheckBox.checked(!newConfig.hideCount());
                showItemCheckBox.checked(!newConfig.hideItem());
                showNameCheckBox.checked(!newConfig.hideName());
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
        var displayConfig = new DisplayingPanelItem.Config();
        boolean locked = false;

        if (lockedCheckbox != null) locked = lockedCheckbox.checked();
        if (showNameCheckBox != null) displayConfig.hideName(!showNameCheckBox.checked());
        if (showItemCheckBox != null) displayConfig.hideItem(!showItemCheckBox.checked());
        if (showCountCheckBox != null) displayConfig.hideCount(!showCountCheckBox.checked());

        handler.sendMessage(new PanelConfigSreenHandler.ConfigConfig(displayConfig, locked));
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