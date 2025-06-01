package com.chyzman.chowl.core.multipart.api.client;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PartRendererFactories {
	private static final Map<PartType<?>, PartRendererFactory<?>> FACTORIES = Maps.newHashMap();

	public static <T extends Part> void register(PartType<? extends T> type, PartRendererFactory<T> factory) {
		FACTORIES.put(type, factory);
	}

	public static Map<PartType<?>, PartRenderer<?>> reload(PartRendererFactory.Context args) {
		Builder<PartType<?>, PartRenderer<?>> builder = ImmutableMap.builder();
		FACTORIES.forEach((type, factory) -> {
			try {
				builder.put(type, factory.create(args));
			} catch (Exception e) {
				throw new IllegalStateException("Failed to create model for " + ChowlRegistries.PART.getId(type), e);
			}
		});
		return builder.build();
	}
}
