package com.chyzman.chowl.core.blockentity.api;

import com.chyzman.chowl.core.multipart.Part;
import com.chyzman.chowl.core.multipart.PartType;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import io.wispforest.endec.SerializationContext;
import io.wispforest.owo.serialization.format.nbt.NbtDeserializer;
import io.wispforest.owo.serialization.format.nbt.NbtSerializer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public abstract class MultipartBlockEntity extends BlockEntity {
    // TODO: yay we have a sorted set of parts, now do magic stuff with it
    private final Set<Part> parts = new TreeSet<>();

    public MultipartBlockEntity(BlockEntityType<? extends MultipartBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        if (nbt.contains("chowl:multipart")) {
            Map<Identifier, ? extends Part> partMap = Part.ENDEC.decode(SerializationContext.empty(), NbtDeserializer.of(nbt.get("chowl:multipart")));
            parts.addAll(partMap.values());
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        Map<Identifier, Part> partMap = new HashMap<>();
        for (Part part : parts) {
            partMap.put(part.getId(), part);
        }

        NbtSerializer serializer = NbtSerializer.of();
        Part.ENDEC.encode(SerializationContext.empty(), serializer, partMap);
        nbt.put("chowl:multipart", serializer.result());
    }
}
