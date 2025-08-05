package com.chyzman.chowl.oddities.blockentity;


import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.util.VoxelShapeHelper;
import com.chyzman.chowl.oddities.block.ClipboardBlock;
import com.chyzman.chowl.oddities.registry.OdditiesBlockEntities;
import com.chyzman.chowl.oddities.registry.OdditiesComponents;
import com.chyzman.chowl.oddities.registry.OdditiesParts;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.ops.WorldOps;
import io.wispforest.owo.ui.util.UISounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ClipboardBlockEntity extends MultipartHolderBlockEntity {

    public String title = "";
    public List<ClipboardLine> contents = defaultContents();

    public static final KeyedEndec<String> TITLE_ENDEC = Endec.STRING.keyed("title", "");
    public static final KeyedEndec<List<ClipboardLine>> CONTENT_ENDEC = ClipboardLine.ENDEC.listOf().keyed("content", ClipboardBlockEntity::defaultContents);


    public ClipboardBlockEntity(BlockPos pos, BlockState state) {
        super(OdditiesBlockEntities.CLIPBOARD, pos, state);
        for (int i = 0; i < 12; i++) this.addPart(new CheckboxPart(i));
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

    public static class CheckboxPart extends Part {
        private final int index;

        public CheckboxPart(int index) {
            super(null);
            this.index = index;
        }

        @Override
        public VoxelShape getPartOutlineShape(List<Part> otherParts, BlockView world, BlockPos pos, ShapeContext context) {
            var clipboard = world.getBlockState(pos);
            if (!(clipboard.getBlock() instanceof ClipboardBlock)) return VoxelShapes.empty();
            return VoxelShapeHelper.rotate(
                Block.createCuboidShape(3, 13 - index, 1, 4, 14 - index, 1.5),
                clipboard.get(ClipboardBlock.ORIENTATION)
            );
        }

        @Override
        public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
            if (player.getStackInHand(player.getActiveHand()).isEmpty()) {
                var blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof ClipboardBlockEntity clipboard) {
                    if (clipboard.contents.size() > index) {
                        var line = clipboard.contents.get(index);
                        line.checked = !line.checked;
                        UISounds.playButtonSound();
                        clipboard.markDirty();
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.PASS;
        }
    }
}
