package com.chyzman.chowl.electromechanics;

import com.chyzman.chowl.core.ChowlItemGroup;
import com.chyzman.chowl.electromechanics.registry.*;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

import static net.minecraft.world.item.Items.REDSTONE;

public class Electromechanics implements ModInitializer {
    public static final String MODID = "chowl-electromechanics";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @Override
    public void onInitialize() {
        ElectromechanicsItems.init();
        ElectromechanicsBlocks.init();
        ElectromechanicsBlockEntities.init();
        ElectromechanicsEventListeners.init();
        FieldRegistrationHandler.register(ElectromechanicsComponents.class, MODID, false);
        FieldRegistrationHandler.register(ElectromechanicsSounds.class, MODID, false);

        ChowlItemGroup.proposeIcon(() -> Icon.of(REDSTONE.asItem()), 80);

        ChowlItemGroup.addInitializer(group -> {
            group.addCustomTab(Icon.of(REDSTONE.asItem()), "electromechanics", (context, entries) -> {
                entries.accept(ElectromechanicsItems.KNOCKER);
                entries.accept(ElectromechanicsItems.WATCHER);
            }, false);
        }, 70);
    }
}
