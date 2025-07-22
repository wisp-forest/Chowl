package com.chyzman.chowl.core.multipart.impl;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.registry.CoreParts;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import net.minecraft.block.Block;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.List;

public class FramePart extends Part {
    public static final StructEndec<FramePart> ENDEC = Endec.unit(FramePart::new);
    public static final VoxelShape OUTLINE = VoxelShapes.combine(VoxelShapes.fullCube(), VoxelShapes.union(
      Block.createCuboidShape(2, 0, 2, 14, 16, 14),
      Block.createCuboidShape(0, 2, 2, 16, 14, 14),
      Block.createCuboidShape(2, 2, 0, 14, 14, 16)
    ), (a, b) -> a && !b);

    public FramePart() {
        super(CoreParts.FRAME);
    }

    @Override
    public VoxelShape getPartOutlineShape(List<Part> otherParts, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE;
    }
}
