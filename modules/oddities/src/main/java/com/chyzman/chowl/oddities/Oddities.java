package com.chyzman.chowl.oddities;

import com.chyzman.chowl.core.ChowlItemGroup;
import com.chyzman.chowl.oddities.registry.*;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Oddities implements ModInitializer {
    public static final String MODID = "chowl-oddities";

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }

    @Override
    public void onInitialize() {
        OdditiesItems.init();
        OdditiesBlocks.init();
        OdditiesBlockEntities.init();
        OdditiesEventListeners.init();
        OdditiesAttachables.init();
        com.chyzman.chowl.oddities.registry.OdditiesParts.init();
        FieldRegistrationHandler.register(OdditiesComponents.class, MODID, false);
        FieldRegistrationHandler.register(OdditiesSounds.class, MODID, false);


        ChowlItemGroup.proposeIcon(() -> Icon.of(OdditiesItems.CAUTION_BLOCK), 80);

        ChowlItemGroup.addInitializer(group -> {
            group.addCustomTab(Icon.of(OdditiesItems.CAUTION_BLOCK), "oddities", (context, entries) -> {
                entries.add(OdditiesItems.CAUTION_BLOCK);
                entries.add(OdditiesItems.CLIPBOARD);
                entries.add(OdditiesItems.STICKY_NOTE);
                entries.add(OdditiesItems.PIN);
                entries.add(OdditiesItems.STRING);
                entries.add(OdditiesItems.CLOCK);
                entries.add(OdditiesItems.CALENDAR);
            }, false);
        }, 60);
    }
}

//TODO: do block buttons by using shape context's entity to determine if it's the client then use cursorTarget to find closest button
