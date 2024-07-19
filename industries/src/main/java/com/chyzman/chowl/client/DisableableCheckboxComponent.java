package com.chyzman.chowl.client;

import io.wispforest.owo.ui.component.SmallCheckboxComponent;
import net.minecraft.text.Text;

public class DisableableCheckboxComponent extends SmallCheckboxComponent {
    private boolean disabled;

    public DisableableCheckboxComponent(Text label) {
        super(label);
    }

    @Override
    public void toggle() {
        if (disabled) return;

        super.toggle();
    }

    public boolean disabled() {
        return disabled;
    }

    public DisableableCheckboxComponent disabled(boolean disabled) {
        this.disabled = disabled;

        return this;
    }
}
