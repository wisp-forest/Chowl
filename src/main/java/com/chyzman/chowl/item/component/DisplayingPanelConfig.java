package com.chyzman.chowl.item.component;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public record DisplayingPanelConfig(boolean hideCount, boolean hideCapacity, boolean hideName, boolean hideItem,
                                    boolean hideUpgrades, boolean hideButtons, boolean showPercentage,
                                    boolean ignoreTemplating, Style textStyle) {
    private static final Style DEFAULT_STYLE = Style.EMPTY.withColor(Formatting.WHITE);

    public static final Endec<DisplayingPanelConfig> ENDEC = StructEndecBuilder.of(
        Endec.BOOLEAN.fieldOf("HideCount", DisplayingPanelConfig::hideCount),
        Endec.BOOLEAN.fieldOf("HideCapacity", DisplayingPanelConfig::hideCapacity),
        Endec.BOOLEAN.fieldOf("HideName", DisplayingPanelConfig::hideName),
        Endec.BOOLEAN.fieldOf("HideItem", DisplayingPanelConfig::hideItem),
        Endec.BOOLEAN.fieldOf("HideUpgrades", DisplayingPanelConfig::hideUpgrades),
        Endec.BOOLEAN.fieldOf("HideButtons", DisplayingPanelConfig::hideButtons),
        Endec.BOOLEAN.fieldOf("ShowPercentage", DisplayingPanelConfig::showPercentage),
        Endec.BOOLEAN.fieldOf("IgnoreTemplating", DisplayingPanelConfig::ignoreTemplating),
        // TODO: use the CodecUtils.toEndec overload for both Codec and PacketCodec later
        CodecUtils.toEndec(Style.Codecs.CODEC).optionalFieldOf("TextStyle", DisplayingPanelConfig::textStyle, DEFAULT_STYLE),
        DisplayingPanelConfig::new
    );

    public static final DisplayingPanelConfig DEFAULT = new DisplayingPanelConfig(false, false, false, false, false, false, false, false, DEFAULT_STYLE);

    public Builder toBuilder() {
        return new Builder()
            .hideCount(this.hideCount)
            .hideCapacity(this.hideCapacity)
            .hideName(this.hideName)
            .hideItem(this.hideItem)
            .hideUpgrades(this.hideUpgrades)
            .hideButtons(this.hideButtons)
            .showPercentage(this.showPercentage)
            .ignoreTemplating(this.ignoreTemplating)
            .textStyle(this.textStyle);
    }

    public static class Builder {
        private boolean hideCount = false;
        private boolean hideCapacity = false;
        private boolean hideName = false;
        private boolean hideItem = false;
        private boolean hideUpgrades = false;
        private boolean hideButtons = false;
        private boolean showPercentage = false;
        private boolean ignoreTemplating = false;
        private Style textStyle = DEFAULT_STYLE;

        public Builder hideCount(boolean hideCount) {
            this.hideCount = hideCount;
            return this;
        }

        public Builder hideCapacity(boolean hideCapacity) {
            this.hideCapacity = hideCapacity;
            return this;
        }

        public Builder hideName(boolean hideName) {
            this.hideName = hideName;
            return this;
        }

        public Builder hideItem(boolean hideItem) {
            this.hideItem = hideItem;
            return this;
        }

        public Builder hideUpgrades(boolean hideUpgrades) {
            this.hideUpgrades = hideUpgrades;
            return this;
        }

        public Builder hideButtons(boolean hideButtons) {
            this.hideButtons = hideButtons;
            return this;
        }

        public Builder showPercentage(boolean showPercentage) {
            this.showPercentage = showPercentage;
            return this;
        }

        public Builder ignoreTemplating(boolean ignoreTemplating) {
            this.ignoreTemplating = ignoreTemplating;
            return this;
        }

        public Builder textStyle(Style textStyle) {
            this.textStyle = textStyle;
            return this;
        }

        public DisplayingPanelConfig build() {
            return new DisplayingPanelConfig(hideCount, hideCapacity, hideName, hideItem, hideUpgrades, hideButtons, showPercentage, ignoreTemplating, textStyle);
        }
    }
}
