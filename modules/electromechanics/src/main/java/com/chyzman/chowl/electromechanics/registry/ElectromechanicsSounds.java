package com.chyzman.chowl.electromechanics.registry;

import com.chyzman.chowl.electromechanics.Electromechanics;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class ElectromechanicsSounds implements AutoRegistryContainer<SoundEvent> {

    public static final SoundEvent BLOCK_KNOCKER_CLICK = SoundEvent.createVariableRangeEvent(Electromechanics.id("block.knocker.click"));

    @Override
    public Registry<SoundEvent> getRegistry() {
        return BuiltInRegistries.SOUND_EVENT;
    }

    @Override
    public Class<SoundEvent> getTargetFieldType() {
        return SoundEvent.class;
    }
}
