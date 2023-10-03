package com.chyzman.chowl.screen;

import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.component.SmallCheckboxComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static com.chyzman.chowl.item.DrawerPanelItem.COMPONENT;
import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

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
        final com.chyzman.chowl.item.DrawerComponent[] component = {handler.stack.get().get(COMPONENT)};
        var filterQuoteSlotUnQuote = new FakeSlotComponent(component[0].itemVariant.toStack())
                .<FakeSlotComponent>configure(fakeSlot -> {
                    fakeSlot.mouseDown().subscribe((mouseX, mouseY, button) -> {
                        if (button == 0) {
                            handler.sendMessage(new PanelConfigSreenHandler.ConfigFilter(handler.getCursorStack()));
                        }
                        return true;
                    });
                    fakeSlot.setTooltipFromStack(true);
                    fakeSlot.id("filter-slot");
                });
        var lockedCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.locked"))
                .<SmallCheckboxComponent>configure(smallCheckboxComponent -> {
                    smallCheckboxComponent.checked(component[0].config.locked);
                    smallCheckboxComponent.onChanged().subscribe(nowChecked -> {
                        handler.sendMessage(new PanelConfigSreenHandler.ConfigConfig(nowChecked, !component[0].config.hideCount, !component[0].config.hideItem, !component[0].config.hideName));
                    });
                });
        var showCountCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.show_count"))
                .<SmallCheckboxComponent>configure(smallCheckboxComponent -> {
                    smallCheckboxComponent.checked(!component[0].config.hideCount);
                    smallCheckboxComponent.onChanged().subscribe(nowChecked -> {
                        handler.sendMessage(new PanelConfigSreenHandler.ConfigConfig(component[0].config.locked, nowChecked, !component[0].config.hideItem, !component[0].config.hideName));
                    });
                });
        var showItemCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.show_item"))
                .<SmallCheckboxComponent>configure(smallCheckboxComponent -> {
                    smallCheckboxComponent.checked(!component[0].config.hideItem);
                    smallCheckboxComponent.onChanged().subscribe(nowChecked -> {
                        handler.sendMessage(new PanelConfigSreenHandler.ConfigConfig(component[0].config.locked, !component[0].config.hideCount, nowChecked, !component[0].config.hideName));
                    });
                });
        var showNameCheckBox = Components.smallCheckbox(Text.translatable("ui.chowl-industries.config_panel.show_name"))
                .<SmallCheckboxComponent>configure(smallCheckboxComponent -> {
                    smallCheckboxComponent.checked(!component[0].config.hideName);
                    smallCheckboxComponent.onChanged().subscribe(nowChecked -> {
                        handler.sendMessage(new PanelConfigSreenHandler.ConfigConfig(component[0].config.locked, !component[0].config.hideCount, !component[0].config.hideItem, nowChecked));
                    });
                });

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
                    flowLayout.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                            .<FlowLayout>configure(flow -> {
                                flow.margins(Insets.bottom(1));
                                flow.verticalAlignment(VerticalAlignment.CENTER);
                            })
                            .child(filterQuoteSlotUnQuote.margins(Insets.of(1).withRight(3)))
                            .child(Components.texture(id("textures/gui/container/slot.png"), 0, 0, 18, 18, 18, 18)
                                    .<TextureComponent>configure(textureComponent -> {
                                        textureComponent.positioning(Positioning.absolute(0, 0));
                                    }))
                            .child(Components.label(Text.translatable("ui.chowl-industries.config_panel.filter"))
                            ));
                    flowLayout.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                            .<FlowLayout>configure(flow -> {
                                flow.margins(Insets.bottom(1));
                                flow.verticalAlignment(VerticalAlignment.CENTER);
                            })
                            .child(lockedCheckBox));
                    flowLayout.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                            .<FlowLayout>configure(flow -> {
                                flow.margins(Insets.bottom(1));
                                flow.verticalAlignment(VerticalAlignment.CENTER);
                            })
                            .child(showCountCheckBox));
                    flowLayout.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                            .<FlowLayout>configure(flow -> {
                                flow.margins(Insets.bottom(1));
                                flow.verticalAlignment(VerticalAlignment.CENTER);
                            })
                            .child(showItemCheckBox));
                    flowLayout.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18))
                            .<FlowLayout>configure(flow -> {
                                flow.margins(Insets.bottom(1));
                                flow.verticalAlignment(VerticalAlignment.CENTER);
                            })
                            .child(showNameCheckBox));
                    flowLayout.child(inventoryFlow);
                });


        handler.stack.observe(stack -> {
            component[0] = stack.get(COMPONENT);
            filterQuoteSlotUnQuote.stack(component[0].itemVariant.toStack());
            lockedCheckBox.checked(component[0].config.locked);
            showCountCheckBox.checked(!component[0].config.hideCount);
            showItemCheckBox.checked(!component[0].config.hideItem);
            showNameCheckBox.checked(!component[0].config.hideName);
        });

        rootComponent.child(Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100))
                .child(Components.label(Text.translatable("ui.chowl-industries.config_panel.title")))
                .child(verticalFlow)
                .surface(Surface.VANILLA_TRANSLUCENT)
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER)
        );
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