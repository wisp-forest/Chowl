package com.chyzman.chowl.oddities.blockentity;


import com.chyzman.chowl.oddities.registry.OdditiesBlockEntities;
import com.chyzman.chowl.oddities.registry.OdditiesComponents;
import io.wispforest.owo.ops.WorldOps;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.format.nbt.NbtDeserializer;
import io.wispforest.owo.serialization.format.nbt.NbtSerializer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ClipboardBlockEntity extends BlockEntity {

//    public Text title = Text.empty();
//    public List<Text> content = Text.empty();

    public Text content = Text.empty();


    public ClipboardBlockEntity(BlockPos pos, BlockState state) {
        super(OdditiesBlockEntities.CLIPBOARD, pos, state);
    }

    @Override
    protected void addComponents(ComponentMap.Builder components) {
        super.addComponents(components);

        components.add(OdditiesComponents.CLIPBOARD_CONTENT, this.content);
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);

        this.content = components.get(OdditiesComponents.CLIPBOARD_CONTENT);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createNbt(registries);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        if (nbt.contains("content")) this.content = CodecUtils
                .toEndec(OdditiesComponents.CLIPBOARD_CONTENT.getCodecOrThrow())
                .decodeFully(NbtDeserializer::of, nbt.get("content")
                );
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        if (this.content != null) nbt.put("content", CodecUtils
                .toEndec(OdditiesComponents.CLIPBOARD_CONTENT.getCodecOrThrow())
                .encodeFully(NbtSerializer::of, this.content)
        );
    }

    @Override
    public void markDirty() {
        super.markDirty();
        WorldOps.updateIfOnServer(world, pos);
    }
}
