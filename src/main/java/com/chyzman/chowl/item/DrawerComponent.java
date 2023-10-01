package com.chyzman.chowl.item;

import com.google.gson.Gson;
import io.wispforest.owo.nbt.NbtKey;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.math.BigInteger;
import java.time.format.TextStyle;

import static net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix.GSON;

@SuppressWarnings("UnstableApiUsage")
public class DrawerComponent {
    public static final NbtKey.Type<DrawerComponent> KEY_TYPE = NbtKey.Type.COMPOUND.then(compound -> {
        var component = new DrawerComponent();
        component.readNbt(compound);
        return component;
    }, component -> {
        var tag = new NbtCompound();
        component.writeNbt(tag);
        return tag;
    });

    public ItemVariant itemVariant = ItemVariant.blank();
    public BigInteger count = BigInteger.ZERO;
    public DrawerConfig config = new DrawerConfig();

    public DrawerComponent() {
    }

    public DrawerComponent(ItemVariant itemVariant, BigInteger count) {
        this.itemVariant = itemVariant;
        this.count = count;
    }

    public int insert(ItemStack stack) {
        if (this.itemVariant.isBlank())
            this.itemVariant = ItemVariant.of(stack);

        if (this.itemVariant.matches(stack)) {
            this.count = this.count.add(BigInteger.valueOf(stack.getCount()));
            return 0;
        } else {
            return stack.getCount();
        }
    }

    public ItemStack extract(int count) {
        int actualCount;
        try {
            actualCount = Math.min(this.count.intValueExact(), count);
        } catch (ArithmeticException e) {
            actualCount = count;
        }
        this.count = this.count.subtract(BigInteger.valueOf(actualCount));
        var temp = itemVariant.toStack();
        temp.setCount(actualCount);
        updateVariant();
        return temp;
    }

    public void updateVariant() {
        if (this.count.compareTo(BigInteger.ZERO) <= 0 && !config.locked) {
            setVariant(ItemVariant.blank());
        }
    }

    public boolean setVariant(ItemVariant itemVariant) {
        var returned = this.itemVariant.equals(itemVariant);
        this.itemVariant = itemVariant;
        return returned;
    }

    public void readNbt(NbtCompound nbt) {
        this.itemVariant = ItemVariant.fromNbt(nbt.getCompound("Variant"));
        this.count = !nbt.getString("Count").isBlank() ? new BigInteger(nbt.getString("Count")) : BigInteger.ZERO;
        config.readNbt(nbt.getCompound("DrawerConfig"));
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.put("Variant", itemVariant.toNbt());
        nbt.putString("Count", count.toString());
        NbtCompound drawerConfig = new NbtCompound();
        config.writeNbt(drawerConfig);
        nbt.put("DrawerConfig", drawerConfig);
    }

    public MutableText styleText(MutableText text) {
        if (config.textStyle == null || config.textStyle.isEmpty()) return text;
        return text.setStyle(config.textStyle);
    }

    public MutableText styleText(String string) {
        return styleText(Text.literal(string));
    }

    public static class DrawerConfig {
        public boolean locked = false;
        public boolean hideCount = false;
        public boolean hideName = false;
        public boolean hideItem = false;
        public Style textStyle = Style.EMPTY.withColor(Formatting.WHITE);

        public void readNbt(NbtCompound nbt) {
            this.locked = nbt.getBoolean("Locked");
            this.hideCount = nbt.getBoolean("HideCount");
            this.hideName = nbt.getBoolean("HideName");
            this.hideItem = nbt.getBoolean("HideItem");
            this.textStyle = GSON.fromJson(nbt.getString("TextStyle"), Style.class);
        }

        public void writeNbt(NbtCompound nbt) {
            nbt.putBoolean("Locked", locked);
            nbt.putBoolean("HideCount", hideCount);
            nbt.putBoolean("HideName", hideName);
            nbt.putBoolean("HideItem", hideItem);
            nbt.putString("TextStyle", (textStyle == null ? Style.EMPTY : textStyle).toString());
        }
    }
}