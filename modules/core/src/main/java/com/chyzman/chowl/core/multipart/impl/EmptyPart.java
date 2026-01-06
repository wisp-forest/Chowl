package com.chyzman.chowl.core.multipart.impl;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.registry.CoreParts;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmptyPart extends Part {
    private static EmptyPart INSTANCE;
    public static final StructEndec<EmptyPart> ENDEC = Endec.unit(EmptyPart::getInstance);

    public EmptyPart() {
        super(CoreParts.EMPTY);
    }

    public static EmptyPart getInstance() {
        if (INSTANCE == null) INSTANCE = new EmptyPart();
        return INSTANCE;
    }

    @Override
    public VoxelShape getPartOutlineShape(List<Part> otherParts, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }
}
