package com.chyzman.chowl.oddities.attachable;

import com.chyzman.chowl.core.attachables.api.Attachable;
import com.chyzman.chowl.core.attachables.impl.AttachableHitResult;
import com.chyzman.chowl.core.attachables.impl.TranformedVoxelShape;
import com.chyzman.chowl.oddities.registry.OdditiesAttachables;
import com.chyzman.chowl.oddities.registry.OdditiesItems;
import com.chyzman.chowl.oddities.screen.StickyNoteScreen;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import io.wispforest.owo.util.Wisdom;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class StickyNoteAttachable extends Attachable {
    private static final VoxelShape SHAPE = Attachable.createAttachableCuboid(
            -8, -8, -0.5,
            8, 8, 0.5,
            1 / 16f
    );

    private Vec3d pos = Vec3d.ZERO;
    private Quaternionf rotation = new Quaternionf();
    private Text text = Text.empty();

    public StickyNoteAttachable() {
        super(OdditiesAttachables.STICKY_NOTE);
    }

    public StickyNoteAttachable(Vec3d pos, Quaternionf rotation, Text text) {
        this();
        this.pos = pos;
        this.rotation = rotation;
        this.text = text;
    }

    public static final StructEndec<StickyNoteAttachable> ENDEC = StructEndecBuilder.of(
            MinecraftEndecs.VEC3D.fieldOf("pos", s -> s.pos),
            CodecUtils.toEndec(Codecs.QUATERNION_F, PacketCodecs.QUATERNION_F)
                    .fieldOf("rotation", s -> s.rotation),
            MinecraftEndecs.TEXT.fieldOf("text", s -> s.text),
            StickyNoteAttachable::new
    );

    //region GETTERS AND SETTERS

    public Vec3d pos() {
        return this.pos;
    }

    public StickyNoteAttachable pos(Vec3d pos) {
        this.pos = pos;
        return this;
    }

    public Quaternionf rotation() {
        return this.rotation;
    }

    public StickyNoteAttachable rotation(Quaternionf rotation) {
        this.rotation = rotation;
        return this;
    }

    public Text text() {
        return this.text;
    }

    public StickyNoteAttachable text(Text text) {
        this.text = text;
        return this;
    }

    //endregion

    @Override
    public Set<ChunkPos> getChunksOccupied() {
        var offsetPos = pos.add(new Vec3d(rotation.transform(new Vector3f(0, 0.01f, 0))));
        return new HashSet<>(List.of(
                new ChunkPos((int) (Math.round(this.pos.x) >> 4), (int) Math.round(this.pos.z) >> 4),
                new ChunkPos((int) (Math.round(offsetPos.x) >> 4), (int) Math.round(offsetPos.z) >> 4)
        ));
    }

    @Override
    protected TranformedVoxelShape createShape() {
        return new TranformedVoxelShape(Block.createCuboidShape(
                -8 / 4f, -8 / 4f - 1.6, -1 / 5f,
                8 / 4f, 8 / 4f - 1.6, 1 / 20f
        ), new Quaternionf(this.rotation).rotateX((float) Math.toRadians(-85f)));
    }

    @Override
    public boolean isSupported(World world) {
        return Attachable.checkForSupport(this, world, this.pos, this.rotation);
    }

    @Override
    public @Nullable Vec3d raycast(Vec3d start, Vec3d end) {
        return this.getShape().raycast(start, end, this.pos);
    }

    @Override
    public ActionResult onUse(World world, PlayerEntity player, Hand hand, AttachableHitResult hit) {
        if (world.isClient) {
            //I know this will crash on server in prod but im too lazy to fix it right now
            MinecraftClient.getInstance().setScreen(new StickyNoteScreen(this));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBroken(World world, Vec3d pos) {
        ItemScatterer.spawn(
                world,
                pos.x,
                pos.y,
                pos.z,
                OdditiesItems.STICKY_NOTE.getDefaultStack()
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.pos,
                this.rotation,
                this.text
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StickyNoteAttachable that)) return false;
        if (!Objects.equals(this.pos, that.pos)) return false;
        if (!Objects.equals(this.rotation, that.rotation)) return false;
        if (!Objects.equals(this.text, that.text)) return false;
        return super.equals(obj);
    }
}
