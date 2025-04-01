package com.chyzman.chowl.oddities.registry;

import com.chyzman.chowl.oddities.Oddities;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

public class OdditiesSounds implements AutoRegistryContainer<SoundEvent> {

    public static final SoundEvent BLOCK_KNOCKER_CLICK = SoundEvent.of(Oddities.id("block.knocker.click"));

    @Override
    public Registry<SoundEvent> getRegistry() {
        return Registries.SOUND_EVENT;
    }

    @Override
    public Class<SoundEvent> getTargetFieldType() {
        return SoundEvent.class;
    }
}
