package com.chyzman.chowl.item.component;

import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.math.BigInteger;

import static net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix.GSON;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public interface DrawerCustomizationHolder<D extends DrawerCustomizationHolder<D>> {
    NbtKey<DrawerCustomizationComponent> CUSTOMIZATION_COMPONENT = new NbtKey<>("DrawerCustomization", DrawerCustomizationComponent.KEY_TYPE);

    default DrawerCustomizationComponent customizationComponent(ItemStack stack) {
        return stack.get(CUSTOMIZATION_COMPONENT);
    }

    default D showCount(ItemStack stack, boolean showCount) {
        stack.put(CUSTOMIZATION_COMPONENT, stack.get(CUSTOMIZATION_COMPONENT).showCount(showCount));
        return (D) this;
    }

    default boolean showCount(ItemStack stack) {
        return stack.get(CUSTOMIZATION_COMPONENT).showCount();
    }

    default D showName(ItemStack stack, boolean showName) {
        stack.put(CUSTOMIZATION_COMPONENT, stack.get(CUSTOMIZATION_COMPONENT).showName(showName));
        return (D) this;
    }

    default boolean showName(ItemStack stack) {
        return stack.get(CUSTOMIZATION_COMPONENT).showName();
    }

    default D showItem(ItemStack stack, boolean showItem) {
        stack.put(CUSTOMIZATION_COMPONENT, stack.get(CUSTOMIZATION_COMPONENT).showItem(showItem));
        return (D) this;
    }

    default boolean showItem(ItemStack stack) {
        return stack.get(CUSTOMIZATION_COMPONENT).showItem();
    }

    default D textStyle(ItemStack stack, Style style) {
        stack.put(CUSTOMIZATION_COMPONENT, stack.get(CUSTOMIZATION_COMPONENT).textStyle(style));
        return (D) this;
    }

    default Style textStyle(ItemStack stack) {
        return stack.get(CUSTOMIZATION_COMPONENT).textStyle();
    }

    class DrawerCustomizationComponent {
        public static final NbtKey.Type<DrawerCustomizationComponent> KEY_TYPE = NbtKey.Type.COMPOUND.then(compound -> {
            var component = new DrawerCustomizationComponent();
            component.readNbt(compound);
            return component;
        }, component -> {
            var tag = new NbtCompound();
            component.writeNbt(tag);
            return tag;
        });

        boolean hideCount = false;
        boolean hideName = false;
        boolean hideItem = false;
        public Style textStyle = Style.EMPTY.withColor(Formatting.WHITE);

        public DrawerCustomizationComponent() {
        }

        public DrawerCustomizationComponent showCount(boolean show) {
            this.hideCount = !show;
            return this;
        }

        public boolean showCount() {
            return !hideCount;
        }

        public DrawerCustomizationComponent showName(boolean show) {
            this.hideName = !show;
            return this;
        }

        public boolean showName() {
            return !hideName;
        }

        public DrawerCustomizationComponent showItem(boolean show) {
            this.hideItem = !show;
            return this;
        }

        public boolean showItem() {
            return !hideItem;
        }

        public DrawerCustomizationComponent textStyle(Style style) {
            this.textStyle = style;
            return this;
        }

        public Style textStyle() {
            return textStyle;
        }

        public void readNbt(NbtCompound nbt) {
            this.hideCount = nbt.getBoolean("HideCount");
            this.hideName = nbt.getBoolean("HideName");
            this.hideItem = nbt.getBoolean("HideItem");
            this.textStyle = GSON.fromJson(nbt.getString("TextStyle"), Style.class);
        }

        public void writeNbt(NbtCompound nbt) {
            nbt.putBoolean("HideCount", hideCount);
            nbt.putBoolean("HideName", hideName);
            nbt.putBoolean("HideItem", hideItem);
            nbt.putString("TextStyle", (textStyle == null ? Style.EMPTY : textStyle).toString());
        }
    }
}