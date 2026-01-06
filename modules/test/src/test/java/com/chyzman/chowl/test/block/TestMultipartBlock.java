package com.chyzman.chowl.test.block;

import com.chyzman.chowl.core.multipart.api.Multipart;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.test.multipart.TestPart;
import com.chyzman.chowl.test.registry.TestParts;
import net.minecraft.world.level.block.Block;

public class TestMultipartBlock extends Block implements Multipart<TestPart> {
    public TestMultipartBlock(Properties properties) {
        super(properties);
    }

    @Override
    public PartType<TestPart> getPart() {
        return TestParts.TEST_PART;
    }
}
