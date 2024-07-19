package com.chyzman.chowl.industries.event;

import com.chyzman.chowl.industries.transfer.PanelStorageContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PanelEmptiedEvent {
    Event<PanelEmptiedEvent> EVENT = EventFactory.createArrayBacked(PanelEmptiedEvent.class, callbacks -> ctx -> {
        for (var cb : callbacks) {
            cb.onPanelEmptied(ctx);
        }
    });

    void onPanelEmptied(PanelStorageContext ctx);
}
