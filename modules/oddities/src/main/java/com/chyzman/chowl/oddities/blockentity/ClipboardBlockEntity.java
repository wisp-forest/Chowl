package com.chyzman.chowl.oddities.blockentity;


import com.chyzman.chowl.oddities.registry.OdditiesBlockEntities;
import com.chyzman.chowl.oddities.registry.OdditiesComponents;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.ops.WorldOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ClipboardBlockEntity extends BlockEntity {

    public String title = "";
    public List<ClipboardLine> contents = defaultContents();

    public static final KeyedEndec<String> TITLE_ENDEC = Endec.STRING.keyed("title", "");
    public static final KeyedEndec<List<ClipboardLine>> CONTENT_ENDEC = ClipboardLine.ENDEC.listOf().keyed("content", ClipboardBlockEntity::defaultContents);


    public ClipboardBlockEntity(BlockPos pos, BlockState state) {
        super(OdditiesBlockEntities.CLIPBOARD, pos, state);
    }

    public static List<ClipboardLine> defaultContents() {
        List<ClipboardLine> lines = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            lines.add(new ClipboardLine("", false));
        }
        return lines;
    }

    @Override
    protected void addComponents(ComponentMap.Builder components) {
        super.addComponents(components);

        components.add(DataComponentTypes.CUSTOM_NAME, Text.literal(this.title));
        components.add(OdditiesComponents.CLIPBOARD_CONTENT, this.contents);
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);

        this.title = components.getOrDefault(DataComponentTypes.CUSTOM_NAME, Text.empty()).getString();
        this.contents = components.get(OdditiesComponents.CLIPBOARD_CONTENT);
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
        if (nbt.contains("title")) this.title = nbt.get(TITLE_ENDEC);
        if (nbt.contains("contents")) this.contents = nbt.get(CONTENT_ENDEC);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.put(TITLE_ENDEC, this.title);
        nbt.put(CONTENT_ENDEC, this.contents);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        WorldOps.updateIfOnServer(world, pos);
    }

    public static class ClipboardLine {
        public String text;
        public boolean checked;

        public ClipboardLine(String text, boolean checked) {
            this.text = text;
            this.checked = checked;
        }

        public static final Endec<ClipboardLine> ENDEC = StructEndecBuilder.of(
            Endec.STRING.fieldOf("text", o -> o.text),
            Endec.BOOLEAN.fieldOf("checked", o -> o.checked),
            ClipboardLine::new
        );
    }
}
