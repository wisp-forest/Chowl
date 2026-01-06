package com.chyzman.chowl.test.multipart;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.test.registry.TestParts;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.swing.text.html.BlockView;
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
    public VoxelShape getPartOutlineShape(List<Part> otherParts, BlockGetter world, BlockPos pos, CollisionContext context) {
        Vec3i endPos = startPos.offset(4, 4, 4);
        return Block.box(startPos.getX(), startPos.getY(), startPos.getZ(), endPos.getX(), endPos.getY(), endPos.getZ());
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPos, getHolder().getBlockPos());
    }
}
