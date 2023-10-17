package com.chyzman.chowl.block;

import com.chyzman.chowl.Chowl;
import com.chyzman.chowl.client.ChowlClient;
import com.chyzman.chowl.item.component.PanelItem;
import com.chyzman.chowl.transfer.PanelStorageContext;
import io.wispforest.owo.ops.WorldOps;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DrawerFrameBlockEntity extends BlockEntity implements SidedStorageBlockEntity, RenderAttachmentBlockEntity {

    public List<Pair<ItemStack, Integer>> stacks = new ArrayList<>(DefaultedList.ofSize(6, new Pair<>(ItemStack.EMPTY, 0)).stream().toList());
    public BlockState templateState = null;
    public BlockState prevTemplateState = null;
    private int transferCooldown = -1;
    private long lastTickTime;

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
    public void collectPanelStorages(Consumer<SlottedStorage<ItemVariant>> storageConsumer) {
        for (int sideId = 0; sideId < 6; sideId++) {
            var ctx = PanelStorageContext.from(this, Direction.byId(sideId));
            if (!(ctx.stack().getItem() instanceof PanelItem panelItem)) continue;

            var storage = panelItem.getStorage(ctx);

            if (storage != null) storageConsumer.accept(storage);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @Nullable Storage<ItemVariant> getItemStorage(Direction fromSide) {
        var ctx = PanelStorageContext.from(this, fromSide);

        if (!(ctx.stack().getItem() instanceof PanelItem panelItem)) return null;

        return panelItem.getStorage(ctx);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        WorldOps.updateIfOnServer(world, pos);
    }

    public void readNbt(NbtCompound nbt) {
        if (nbt == null) return;
        super.readNbt(nbt);
        var nbtList = nbt.getList("Inventory", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound compound = (NbtCompound) nbtList.get(i);
            stacks.set(i, new Pair<>(ItemStack.fromNbt(compound.getCompound("Stack")), compound.getInt("Orientation")));
        }

        if (nbt.contains("TemplateState", NbtElement.COMPOUND_TYPE)) {
            templateState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound("TemplateState"));
        }

        if (prevTemplateState != templateState && world != null && world.isClient) {
            ChowlClient.reloadPos(world, pos);
            this.prevTemplateState = templateState;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        var nbtList = new NbtList();
        for (var stack : stacks) {
            var compound = new NbtCompound();
            compound.put("Stack", stack.getLeft().writeNbt(new NbtCompound()));
            compound.putInt("Orientation", stack.getRight());
            nbtList.add(compound);
        }
        nbt.put("Inventory", nbtList);

        if (templateState != null)
            nbt.put("TemplateState", NbtHelper.fromBlockState(templateState));
    }

    @Override
    public @Nullable Object getRenderAttachmentData() {
        return templateState;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        for (Pair<ItemStack, Integer> stored : stacks) {
            if (stored.getLeft().isEmpty()) continue;

        }
    }
}