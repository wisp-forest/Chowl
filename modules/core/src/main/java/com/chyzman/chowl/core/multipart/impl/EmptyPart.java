package com.chyzman.chowl.core.multipart.impl;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.registry.CoreParts;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import java.util.List;

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
    public VoxelShape getPartOutlineShape(List<Part> otherParts, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }
}
