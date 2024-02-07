package com.chyzman.chowl.block;

import com.chyzman.chowl.Chowl;
import com.chyzman.chowl.client.ChowlClient;
import com.chyzman.chowl.item.component.DisplayingPanelItem;
import com.chyzman.chowl.item.component.PanelItem;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.transfer.PanelStorageContext;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.wispforest.owo.ops.WorldOps;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
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
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DrawerFrameBlockEntity extends BlockEntity implements SidedStorageBlockEntity {

    public List<SideState> stacks = new ArrayList<>(DefaultedList.ofSize(6, new SideState(ItemStack.EMPTY, 0, false)).stream().toList());
    public BlockState templateState = null;
    public VoxelShape outlineShape = DrawerFrameBlock.BASE;
    public VoxelShape collisionShape = DrawerFrameBlock.BASE;

    private static final LoadingCache<Integer, VoxelShape> SHAPE_CACHE = CacheBuilder.newBuilder()
        .build(CacheLoader.from(sides -> {
            var shape = DrawerFrameBlock.BASE;

            for (int i = 0; i < 6; i++) {
                if ((sides & (1 << i)) == 0) continue;

                shape = VoxelShapes.union(shape, DrawerFrameBlock.SIDES[i]);
            }

            return shape;
        }));

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
    public @Nullable Storage<ItemVariant> getItemStorage(@Nullable Direction fromSide) {
        if (fromSide == null) {
            List<SlottedStorage<ItemVariant>> storages = new ArrayList<>();

            for (int i = 0; i < 6; i++) {
                var ctx = PanelStorageContext.from(this, Direction.byId(i));

                if (!(ctx.stack().getItem() instanceof PanelItem panelItem)) continue;

                var storage = panelItem.getStorage(ctx);

                if (storage != null) storages.add(storage);
            }

            return new CombinedSlottedStorage<>(storages);
        }

        var ctx = PanelStorageContext.from(this, fromSide);

        if (!(ctx.stack().getItem() instanceof PanelItem panelItem)) return null;

        return panelItem.getStorage(ctx);
    }

    public boolean isSideBaked(int sideId) {
        var side = stacks.get(sideId);

        if (templateState != null && side.stack.getItem() instanceof PanelItem) {
            if (!DisplayingPanelItem.getConfig(side.stack).ignoreTemplating()) return true;
        }

        return side.isBlank;
    }

    private void updateShapes() {
        int collision = 0;
        int outline = 0;

        for (int i = 0; i < stacks.size(); i++) {
            var side = stacks.get(i);

            if (!side.isEmpty()) {
                outline |= (1 << i);

                if (side.stack.getItem() != ChowlRegistry.PHANTOM_PANEL_ITEM)
                    collision |= (1 << i);
            }
        }

        this.collisionShape = SHAPE_CACHE.getUnchecked(collision);
        this.outlineShape = SHAPE_CACHE.getUnchecked(outline);
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
            stacks.set(i, new SideState(
                ItemStack.fromNbt(compound.getCompound("Stack")),
                compound.getInt("Orientation"),
                compound.getBoolean("IsBlank")
            ));
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

    public static void writePanelsToNbt(List<SideState> panels, NbtCompound nbt) {
        var nbtList = new NbtList();
        for (var stack : panels) {
            var compound = new NbtCompound();

            compound.put("Stack", stack.stack.writeNbt(new NbtCompound()));
            compound.putInt("Orientation", stack.orientation);
            compound.putBoolean("IsBlank", stack.isBlank);

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
        for (SideState stored : stacks) {
            if (stored.stack.isEmpty()) continue;

        }
    }

//    @Override
//    public void fillNbt(ItemStack stack, ServerPlayerEntity player) {
//        super.setStackNbt(stack);
//
//        var sides = Direction.getEntityFacingOrder(player);
//        var newPanels = new ArrayList<>(stacks);
//        if (sides[0].getAxis().isHorizontal()) {
//            for (int i = 2; i < 6; i++) {
//                var direction = Direction.fromRotation(360 - Direction.byId(i).asRotation() - sides[0].asRotation());
//                newPanels.set(i, stacks.get(((direction == Direction.EAST || direction == Direction.WEST) ? direction: direction.getOpposite()).getId()));
//            }
//
//            var newBottom = stacks.get(0);
//            if (newBottom.orientation < 4) {
//                if (newBottom.orientation >= 0) {
//                    newBottom.orientation = Math.floorMod(stacks.get(0).orientation - ((int) (sides[0].asRotation() / 90) - 1), 4);
//                    newPanels.set(0, newBottom);
//                }
//            }
//            var newTop = stacks.get(1);
//            if (newTop.orientation < 4 && newTop.orientation >= 0) {
//                newTop.orientation = Math.floorMod(stacks.get(1).orientation - ((int) (sides[0].asRotation() / 90) - 1), 4);
//                newPanels.set(1, newTop);
//            }
//
//            var subNbt = stack.getOrCreateSubNbt("BlockEntityTag");
//            writePanelsToNbt(newPanels, subNbt);
//        }
//    }

    public static final class SideState {
        public ItemStack stack;
        public int orientation;
        public boolean isBlank;

        public SideState(ItemStack stack, int orientation, boolean isBlank) {
            this.stack = stack;
            this.orientation = orientation;
            this.isBlank = isBlank;
        }

        public static SideState empty() {
            return new SideState(ItemStack.EMPTY, 0, false);
        }

        public SideState withStack(ItemStack stack) {
            return new SideState(stack, orientation, isBlank);
        }

        public boolean isEmpty() {
            return stack.isEmpty() && !isBlank;
        }
    }
}