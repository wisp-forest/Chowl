package com.chyzman.chowl.industries.block;

import com.chyzman.chowl.core.blockentity.FrameBlockEntity;
import com.chyzman.chowl.core.blockentity.TemplatableBlockEntity;
import com.chyzman.chowl.industries.item.component.DisplayingPanelItem;
import com.chyzman.chowl.industries.item.component.PanelItem;
import com.chyzman.chowl.industries.registry.ChowlBlocks;
import com.chyzman.chowl.core.registry.ChowlComponents;
import com.chyzman.chowl.industries.registry.ChowlItems;
import com.chyzman.chowl.industries.transfer.PanelStorageContext;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.wispforest.endec.SerializationContext;
import io.wispforest.owo.ops.WorldOps;
import io.wispforest.owo.serialization.RegistriesAttribute;
import io.wispforest.owo.serialization.format.nbt.NbtDeserializer;
import io.wispforest.owo.serialization.format.nbt.NbtSerializer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DrawerFrameBlockEntity extends FrameBlockEntity implements SidedStorageBlockEntity {
    public List<DrawerFrameSideState> stacks = new ArrayList<>(DefaultedList.ofSize(6, DrawerFrameSideState.empty()).stream().toList());
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
        super(ChowlBlocks.Entities.DRAWER_FRAME, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

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

        if (templateState() != null && side.stack().getItem() instanceof PanelItem) {
            if (!DisplayingPanelItem.getConfig(side.stack()).ignoreTemplating()) return true;
        }

        return side.isBlank();
    }

    private void updateShapes() {
        int collision = 0;
        int outline = 0;

        for (int i = 0; i < stacks.size(); i++) {
            var side = stacks.get(i);

            if (!side.isEmpty()) {
                outline |= (1 << i);

                if (side.stack().getItem() != ChowlItems.PHANTOM_PANEL)
                    collision |= (1 << i);
            }
        }

        this.collisionShape = SHAPE_CACHE.getUnchecked(collision);
        this.outlineShape = SHAPE_CACHE.getUnchecked(outline);
    }

    public void scheduleSpreadTemplate() {
        world.scheduleBlockTick(pos, getCachedState().getBlock(), 1);
    }

    public void spreadTemplate() {
        for (int i = 0; i < 6; i++) {
            var possible = pos.offset(Direction.byId(i));

            if (!(world.getBlockEntity(possible) instanceof DrawerFrameBlockEntity other)) continue;
            if (other.templateState() == templateState()) continue;

            other.setTemplateState(templateState());
            other.scheduleSpreadTemplate();
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        WorldOps.updateIfOnServer(world, pos);
        updateShapes();
    }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if (nbt == null) return;

        super.readNbt(nbt, registryLookup);

        var nbtList = nbt.getList("Inventory", NbtElement.COMPOUND_TYPE);
        stacks = DrawerFrameSideState.LIST_ENDEC.decodeFully(
            SerializationContext.attributes(RegistriesAttribute.of((DynamicRegistryManager) registryLookup)),
            NbtDeserializer::of,
            nbtList
        );

        updateShapes();
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        nbt.put("Inventory", DrawerFrameSideState.LIST_ENDEC
            .encodeFully(
                SerializationContext.attributes(RegistriesAttribute.of((DynamicRegistryManager) registryLookup)),
                NbtSerializer::of,
                stacks
            ));
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        for (DrawerFrameSideState stored : stacks) {
            if (stored.stack().isEmpty()) continue;

        }
    }

    @Override
    protected void addComponents(ComponentMap.Builder components) {
        super.addComponents(components);

        components.add(ChowlComponents.DRAWER_FRAME_SIDES, DrawerFrameSideState.copyList(stacks));
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);

        var sidesComponent = components.get(ChowlComponents.DRAWER_FRAME_SIDES);

        if (sidesComponent != null) {
            stacks = DrawerFrameSideState.copyList(sidesComponent);
        }
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);

        nbt.remove("Inventory");
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

}
