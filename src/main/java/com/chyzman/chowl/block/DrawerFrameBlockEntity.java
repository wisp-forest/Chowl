package com.chyzman.chowl.block;

import com.chyzman.chowl.Chowl;
import com.chyzman.chowl.client.ChowlClient;
import com.chyzman.chowl.item.component.DisplayingPanelItem;
import com.chyzman.chowl.item.component.PanelItem;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.transfer.PanelStorageContext;
import io.wispforest.owo.ops.WorldOps;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DrawerFrameBlockEntity extends BlockEntity implements SidedStorageBlockEntity, FillingNbtBlockEntity {

    public List<Pair<ItemStack, Integer>> stacks = new ArrayList<>(DefaultedList.ofSize(6, new Pair<>(ItemStack.EMPTY, 0)).stream().toList());
    public BlockState templateState = null;
    public VoxelShape outlineShape = DrawerFrameBlock.BASE;
    public VoxelShape collisionShape = DrawerFrameBlock.BASE;

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
    public @Nullable Storage<ItemVariant> getItemStorage(Direction fromSide) {
        var ctx = PanelStorageContext.from(this, fromSide);

        if (!(ctx.stack().getItem() instanceof PanelItem panelItem)) return null;

        return panelItem.getStorage(ctx);
    }

    public boolean isSideBaked(int sideId) {
        var sideStack = stacks.get(sideId).getLeft();

        if (templateState != null
            && sideStack.getItem() instanceof PanelItem
            && !DisplayingPanelItem.getConfig(sideStack).ignoreTemplating())
            return true;

        return sideStack.isOf(ChowlRegistry.BLANK_PANEL_ITEM);
    }

    private void updateShapes() {
        this.collisionShape = DrawerFrameBlock.BASE;

        for (int i = 0; i < stacks.size(); i++) {
            var stack = stacks.get(i).getLeft();

            if (stack.isEmpty() || stack.getItem() == ChowlRegistry.PHANTOM_PANEL_ITEM) continue;

            this.collisionShape = VoxelShapes.union(this.collisionShape, DrawerFrameBlock.SIDES[i]);
        }

        this.outlineShape = DrawerFrameBlock.BASE;

        for (int i = 0; i < stacks.size(); i++) {
            var stack = stacks.get(i).getLeft();

            if (stack.isEmpty()) continue;

            this.outlineShape = VoxelShapes.union(this.outlineShape, DrawerFrameBlock.SIDES[i]);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        WorldOps.updateIfOnServer(world, pos);
        updateShapes();
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

        if (world != null && world.isClient) {
            ChowlClient.reloadPos(world, pos);
        }

        updateShapes();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        writePanelsToNbt(stacks, nbt);
        if (templateState != null)
            nbt.put("TemplateState", NbtHelper.fromBlockState(templateState));
    }

    public static void writePanelsToNbt(List<Pair<ItemStack, Integer>> panels, NbtCompound nbt) {
        var nbtList = new NbtList();
        for (var stack : panels) {
            var compound = new NbtCompound();
            compound.put("Stack", stack.getLeft().writeNbt(new NbtCompound()));
            compound.putInt("Orientation", stack.getRight());
            nbtList.add(compound);
        }
        nbt.put("Inventory", nbtList);
    }

    @Override
    public @Nullable Object getRenderData() {
        return templateState;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        for (Pair<ItemStack, Integer> stored : stacks) {
            if (stored.getLeft().isEmpty()) continue;

        }
    }

    @Override
    public void fillNbt(ItemStack stack, ServerPlayerEntity player) {
        super.setStackNbt(stack);

        var sides = Direction.getEntityFacingOrder(player);
        var newPanels = new ArrayList<>(stacks);
        if (sides[0].getAxis().isHorizontal()) {
            for (int i = 2; i < 6; i++) {
                var direction = Direction.fromRotation(360 - Direction.byId(i).asRotation() - sides[0].asRotation());
                newPanels.set(i, stacks.get(((direction == Direction.EAST || direction == Direction.WEST) ? direction: direction.getOpposite()).getId()));
            }

            var newBottom = stacks.get(0);
            if (newBottom.getRight() < 4 && newBottom.getRight() >= 0) {
                newBottom.setRight(Math.floorMod(stacks.get(0).getRight() - ((int) (sides[0].asRotation() / 90) - 1), 4));
                newPanels.set(0, newBottom);
            }
            var newTop = stacks.get(1);
            if (newTop.getRight() < 4 && newTop.getRight() >= 0) {
                newTop.setRight(Math.floorMod(stacks.get(1).getRight() - ((int) (sides[0].asRotation() / 90) - 1), 4));
                newPanels.set(1, newTop);
            }

            var subNbt = stack.getOrCreateSubNbt("BlockEntityTag");
            writePanelsToNbt(newPanels, subNbt);
        }
    }
}