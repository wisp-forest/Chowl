package com.chyzman.chowl.test.registry;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import com.chyzman.chowl.test.ChowlTest;
import com.chyzman.chowl.test.multipart.TestPart;
import net.minecraft.registry.Registry;

public class TestParts {
    public static final PartType<TestPart> TEST_PART = register("test_part", new PartType<>(TestPart.ENDEC, TestPart::new));

    private static <T extends Part> PartType<T> register(String id, PartType<T> partType) {
        return Registry.register(ChowlRegistries.PART, ChowlTest.id(id), partType);
    }

    public static void init() {}
}
