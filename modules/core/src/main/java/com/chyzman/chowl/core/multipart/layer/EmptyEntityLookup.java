package com.chyzman.chowl.core.multipart.layer;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class EmptyEntityLookup implements LevelEntityGetter<Entity> {

	@Override
	public @Nullable Entity get(int id) {
		return null;
	}

	@Override
	public @Nullable Entity get(UUID uuid) {
		return null;
	}

	@Override
	public Iterable<Entity> getAll() {
		return null;
	}

	@Override
	public void get(AABB box, Consumer action) {}

	@Override
	public void get(EntityTypeTest filter, AABB box, AbortableIterationConsumer consumer) {}

	@Override
	public void get(EntityTypeTest filter, AbortableIterationConsumer consumer) {}
}
