package com.chyzman.chowl.item.component;

import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.math.BigInteger;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public interface DrawerCountHolder<D extends DrawerLockHolder<D>> {
    NbtKey<DrawerCountComponent> COUNT_COMPONENT = new NbtKey<>("DrawerCount", DrawerCountComponent.KEY_TYPE);

    default D count(ItemStack stack, BigInteger count) {
        stack.put(COUNT_COMPONENT, new DrawerCountComponent(count, stack.get(COUNT_COMPONENT).capacity));
        if (count.compareTo(BigInteger.ZERO) <= 0 && this instanceof DrawerFilterHolder<?> filterHolder) {
            if ((this instanceof DrawerLockHolder<?> lockHolder && !lockHolder.locked(stack)) || !(this instanceof DrawerLockHolder<?>)) {
                filterHolder.filter(stack, ItemVariant.blank());
            }
        }
        return (D) this;
    }

    default BigInteger count(ItemStack stack) {
        return stack.get(COUNT_COMPONENT).count;
    }

    default D capacity(ItemStack stack, BigInteger capacity) {
        stack.put(COUNT_COMPONENT, new DrawerCountComponent(stack.get(COUNT_COMPONENT).count, capacity));
        return (D) this;
    }

    default BigInteger capacity(ItemStack stack) {
        return stack.get(COUNT_COMPONENT).capacity;
    }

    class DrawerCountComponent {
        static NbtKey.Type<DrawerCountComponent> KEY_TYPE = NbtKey.Type.COMPOUND.then(compound -> {
            var component = new DrawerCountComponent();
            component.readNbt(compound);
            return component;
        }, component -> {
            var tag = new NbtCompound();
            component.writeNbt(tag);
            return tag;
        });

        BigInteger count = BigInteger.ZERO;
        BigInteger capacity = BigInteger.ONE;

        DrawerCountComponent() {
        }

        DrawerCountComponent(BigInteger count, BigInteger capacity) {
            this.count = count;
            this.capacity = capacity;
        }

        public void readNbt(NbtCompound nbt) {
            this.count = !nbt.getString("Count").isBlank() ? new BigInteger(nbt.getString("Count")) : BigInteger.ZERO;
            this.capacity = !nbt.getString("Capacity").isBlank() ? new BigInteger(nbt.getString("Capacity")) : BigInteger.ONE;
        }

        public void writeNbt(NbtCompound nbt) {
            nbt.putString("Count", count.toString());
            nbt.putString("Capacity", capacity.toString());
        }
    }
}