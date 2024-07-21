package com.chyzman.chowl.visage.block;

import com.chyzman.chowl.industries.block.DrawerFrameBlock;
import com.chyzman.chowl.industries.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.industries.client.ChowlClient;
import com.chyzman.chowl.visage.registry.VisageBlocks;
import io.wispforest.owo.ops.WorldOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class VisageRenameMeLaterBlockEntity extends BlockEntity {

    public BlockState templateState = null;

    public VisageRenameMeLaterBlockEntity(BlockPos pos, BlockState state) {
        super(VisageBlocks.Entities.RENAME_ME_LATER, pos, state);
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

    public void scheduleSpreadTemplate() {
        world.scheduleBlockTick(pos, getCachedState().getBlock(), 1);
    }

    public void spreadTemplate() {
        for (int i = 0; i < 6; i++) {
            var possible = pos.offset(Direction.byId(i));

            if (!(world.getBlockEntity(possible) instanceof DrawerFrameBlockEntity other)) continue;
            if (other.templateState == templateState) continue;

            other.templateState = templateState;

            world.setBlockState(possible, world.getBlockState(possible)
                    .with(DrawerFrameBlock.LIGHT_LEVEL, templateState != null ? templateState.getLuminance() : 0));
            other.markDirty();
            other.scheduleSpreadTemplate();
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        WorldOps.updateIfOnServer(world, pos);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if (nbt == null) return;

        super.readNbt(nbt, registryLookup);

        if (nbt.contains("TemplateState", NbtElement.COMPOUND_TYPE)) {
            templateState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound("TemplateState"));
        } else {
            templateState = null;
        }

        if (world != null && world.isClient) {
            ChowlClient.reloadPos(world, pos);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        if (templateState != null)
            nbt.put("TemplateState", NbtHelper.fromBlockState(templateState));
        else
            nbt.putString("TemplateState", "me when mojang code");
    }

    @Override
    public @Nullable Object getRenderData() {
        return templateState;
    }

}
