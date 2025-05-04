package com.chyzman.chowl.test.multipart;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.test.registry.TestParts;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import net.minecraft.block.Block;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TestPart extends Part {
    public static final StructEndec<TestPart> ENDEC = Endec.unit(TestPart::new);

    public TestPart() {
        super(TestParts.TEST_PART);
    }

    @Override
    public int compareTo(@NotNull Part o) {
        return 0;
    }

    @Override
    public VoxelShape getOutlineShape(Set<Part> parts, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(6, 6, 6, 10, 10, 10);
    }
}
