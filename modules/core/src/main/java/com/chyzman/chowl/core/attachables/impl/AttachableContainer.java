package com.chyzman.chowl.core.attachables.impl;

import com.chyzman.chowl.core.attachables.api.Attachable;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.BuiltInEndecs;
import io.wispforest.endec.impl.StructEndecBuilder;
import java.util.*;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;

public class AttachableContainer {
    private static final RandomSource RANDOM = RandomSource.create();

    private final Attachable contained;
    protected UUID uuid;
    protected Set<ChunkPos> chunksOccupied;

    public static final StructEndec<AttachableContainer> ENDEC = StructEndecBuilder.of(
            Attachable.ENDEC.flatFieldOf(o -> o.contained),
            BuiltInEndecs.UUID.fieldOf("uuid", s -> s.uuid),
            AttachableContainer::new
    );

    private AttachableContainer(Attachable contained, UUID uuid) {
        this.contained = contained;
        this.uuid = uuid;
        this.chunksOccupied = contained.getChunksOccupied();
    }

    public AttachableContainer(Attachable contained) {
        this(contained, Mth.createInsecureUUID(RANDOM));
    }

    public Attachable getContained() {
        return this.contained;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid, this.contained, this.chunksOccupied);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AttachableContainer that)) return false;
        if (!Objects.equals(this.uuid, that.uuid)) return false;
        if (!Objects.equals(this.contained, that.contained)) return false;
        if (!Objects.equals(this.chunksOccupied, that.chunksOccupied)) return false;
        return true;
    }
}
