package com.chyzman.chowl.item;

import com.google.gson.Gson;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.math.BigInteger;

@SuppressWarnings("UnstableApiUsage")
public class DrawerComponent {
    private static final Gson GSON = new Gson();
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
    public boolean locked = false;
    public boolean hideCount = false;
    public boolean hideName = false;
    public boolean hideItem = false;
    public Style textStyle = Style.EMPTY.withColor(Formatting.WHITE);

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
        int actualCount = Math.min(count, this.count.intValue());
        this.count = this.count.subtract(BigInteger.valueOf(actualCount));
        var temp = itemVariant.toStack();
        temp.setCount(actualCount);
        if (this.count.compareTo(BigInteger.ZERO) <= 0 && !locked) {
            setVariant(ItemVariant.blank());
        }
        return temp;
    }

    public boolean setVariant(ItemVariant itemVariant) {
        var returned = this.itemVariant.equals(itemVariant);
        this.itemVariant = itemVariant;
        return returned;
    }

    public void readNbt(NbtCompound nbt) {
        this.itemVariant = ItemVariant.fromNbt(nbt.getCompound("Variant"));
        this.count = !nbt.getString("Count").isBlank() ? new BigInteger(nbt.getString("Count")) : BigInteger.ZERO;
        this.locked = nbt.getBoolean("Locked");
        this.hideCount = nbt.getBoolean("HideCount");
        this.hideName = nbt.getBoolean("HideName");
        this.hideItem = nbt.getBoolean("HideItem");
        this.textStyle = GSON.fromJson(nbt.getString("TextStyle"), Style.class);
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.put("Variant", itemVariant.toNbt());
        nbt.putString("Count", count.toString());
        nbt.putBoolean("Locked", locked);
        nbt.putBoolean("HideCount", hideCount);
        nbt.putBoolean("HideName", hideName);
        nbt.putBoolean("HideItem", hideItem);
        nbt.putString("TextStyle", (textStyle == null ? Style.EMPTY : textStyle).toString());
    }

    public MutableText styleText(MutableText text) {
        if (textStyle == null) return text;
        return text.setStyle(textStyle);
    }

    public MutableText styleText(String string) {
        return styleText(Text.literal(string));
    }
}