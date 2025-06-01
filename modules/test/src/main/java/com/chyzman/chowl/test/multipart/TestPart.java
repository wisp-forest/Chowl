package com.chyzman.chowl.test.multipart;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.test.registry.TestParts;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import java.util.List;
import java.util.Objects;

public class TestPart extends Part {
    public static final StructEndec<TestPart> ENDEC = StructEndecBuilder.of(
      MinecraftEndecs.VEC3I.fieldOf("startPos", TestPart::getStartPos),
      TestPart::new
    );

    private final Vec3i startPos;

    public TestPart(Vec3i startPos) {
        super(TestParts.TEST_PART);
        this.startPos = startPos;
    }

    public Vec3i getStartPos() {
        return startPos;
    }

    @Override
    public VoxelShape getOutlineShape(List<Part> parts, BlockView world, BlockPos pos, ShapeContext context) {
        Vec3i endPos = startPos.add(4, 4, 4);
        return Block.createCuboidShape(startPos.getX(), startPos.getY(), startPos.getZ(), endPos.getX(), endPos.getY(), endPos.getZ());
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPos, getHolder().getPos());
    }
}
