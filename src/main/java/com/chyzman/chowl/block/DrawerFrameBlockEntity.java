package com.chyzman.chowl.block;

import com.chyzman.chowl.Chowl;
import com.chyzman.chowl.item.DrawerPanelItem;
import io.wispforest.owo.ops.WorldOps;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class DrawerFrameBlockEntity extends BlockEntity implements SidedStorageBlockEntity {

    public ItemStack[] stacks = DefaultedList.ofSize(6, ItemStack.EMPTY).toArray(new ItemStack[6]);

    public DrawerFrameBlockEntity(BlockPos pos, BlockState state) {
        super(Chowl.DRAWER_FRAME_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @Nullable Storage<ItemVariant> getItemStorage(Direction side) {
        var stack = stacks[side.getId()];

        if (stack.isEmpty()) return null;
        if (!(stack.getItem() instanceof DrawerPanelItem panel)) return null;

        return panel.getStorage(stack, this);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        WorldOps.updateIfOnServer(world, pos);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt == null) return;
        var nbtList = nbt.getList("Inventory", NbtElement.COMPOUND_TYPE);
        stacks = new ItemStack[6];
        for (int i = 0; i < nbtList.size(); i++) {
            stacks[i] = ItemStack.fromNbt((NbtCompound) nbtList.get(i));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        var nbtList = new NbtList();
        for (var stack : stacks) {
            nbtList.add(stack.writeNbt(new NbtCompound()));
        }
        nbt.put("Inventory", nbtList);
    }
}