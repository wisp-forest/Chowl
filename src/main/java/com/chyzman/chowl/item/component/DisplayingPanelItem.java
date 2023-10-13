package com.chyzman.chowl.item.component;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.util.NbtKeyTypes;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

// Represents a panel that can display some variant and a count/capacity
@SuppressWarnings("UnstableApiUsage")
public interface DisplayingPanelItem extends PanelItem {
    NbtKey<Config> CONFIG = new NbtKey<>("Config", Config.KEY_TYPE);

    ItemVariant displayedVariant(ItemStack stack);
    BigInteger displayedCount(ItemStack stack, @Nullable DrawerFrameBlockEntity drawerFrame);

    default @Nullable Text styleText(ItemStack stack, Text wrapped) {
        return Text.literal("").append(wrapped).setStyle(stack.get(CONFIG).textStyle());
    }

    class Config {
        public static final NbtKey.Type<Config> KEY_TYPE = NbtKeyTypes.fromFactory(Config::new, Config::readNbt, Config::writeNbt);

        private boolean hideCount = false;
        private boolean hideName = false;
        private boolean hideItem = false;
        private Style textStyle = Style.EMPTY.withColor(Formatting.WHITE);

        public Config() {
        }

        public boolean hideCount() {
            return hideCount;
        }

        public void hideCount(boolean hideCount) {
            this.hideCount = hideCount;
        }

        public boolean hideName() {
            return hideName;
        }

        public void hideName(boolean hideName) {
            this.hideName = hideName;
        }

        public boolean hideItem() {
            return hideItem;
        }

        public void hideItem(boolean hideItem) {
            this.hideItem = hideItem;
        }

        public Style textStyle() {
            return textStyle;
        }

        public void textStyle(Style textStyle) {
            this.textStyle = textStyle;
        }

        public void readNbt(NbtCompound nbt) {
            this.hideCount = nbt.getBoolean("HideCount");
            this.hideName = nbt.getBoolean("HideName");
            this.hideItem = nbt.getBoolean("HideItem");
            this.textStyle = Style.CODEC.parse(NbtOps.INSTANCE, nbt.get("TextStyle"))
                .get()
                .left()
                .orElse(Style.EMPTY.withColor(Formatting.WHITE));
        }

        public void writeNbt(NbtCompound nbt) {
            nbt.putBoolean("HideCount", hideCount);
            nbt.putBoolean("HideName", hideName);
            nbt.putBoolean("HideItem", hideItem);
            nbt.put("TextStyle", Util.getResult(
                Style.CODEC.encodeStart(NbtOps.INSTANCE, textStyle),
                RuntimeException::new
            ));
        }
    }
}
