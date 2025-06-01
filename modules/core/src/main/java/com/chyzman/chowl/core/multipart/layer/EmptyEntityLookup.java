package com.chyzman.chowl.core.multipart.layer;

import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.Box;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityLookup;
import org.jetbrains.annotations.Nullable;

public class EmptyEntityLookup implements EntityLookup<Entity> {

	@Override
	public @Nullable Entity get(int id) {
		return null;
	}

	@Override
	public @Nullable Entity get(UUID uuid) {
		return null;
	}

	@Override
	public Iterable<Entity> iterate() {
		return null;
	}

	@Override
	public void forEachIntersects(Box box, Consumer action) {}

	@Override
	public void forEachIntersects(TypeFilter filter, Box box, LazyIterationConsumer consumer) {}

	@Override
	public void forEach(TypeFilter filter, LazyIterationConsumer consumer) {}
}
