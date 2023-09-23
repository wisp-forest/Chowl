package com.chyzman.chowl.block;

import com.chyzman.chowl.Chowl;
import io.wispforest.owo.ops.WorldOps;
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
import org.jetbrains.annotations.Nullable;

public class DrawerFrameBlockEntity extends BlockEntity {

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