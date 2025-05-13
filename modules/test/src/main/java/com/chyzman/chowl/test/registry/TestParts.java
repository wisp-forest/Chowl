package com.chyzman.chowl.test.registry;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import com.chyzman.chowl.test.ChowlTest;
import com.chyzman.chowl.test.multipart.TestPart;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.Vec3i;

public class TestParts {
    public static final PartType<TestPart> TEST_PART = register("test_part", new PartType<>(TestPart.ENDEC, (parts) -> {
        return switch (parts.size() % 8) {
            case 0 -> new TestPart(new Vec3i(2, 2, 2));
            case 1 -> new TestPart(new Vec3i(8, 2, 2));
            case 2 -> new TestPart(new Vec3i(2, 2, 8));
            case 3 -> new TestPart(new Vec3i(8, 2, 8));
            case 4 -> new TestPart(new Vec3i(2, 8, 2));
            case 5 -> new TestPart(new Vec3i(8, 8, 2));
            case 6 -> new TestPart(new Vec3i(2, 8, 8));
            case 7 -> new TestPart(new Vec3i(8, 8, 8));
            default -> throw new IllegalStateException("Unexpected value: " + parts.size() % 8);
        };
    }));

    private static <T extends Part> PartType<T> register(String id, PartType<T> partType) {
        return Registry.register(ChowlRegistries.PART, ChowlTest.id(id), partType);
    }

    public static void init() {}
}
