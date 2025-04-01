package com.chyzman.chowl.oddities.registry;

import com.chyzman.chowl.core.attachable.Attachable;
import com.chyzman.chowl.core.attachable.AttachableType;
import com.chyzman.chowl.core.attachable.client.AttachableRendererFactories;
import com.chyzman.chowl.oddities.Oddities;
import com.chyzman.chowl.oddities.attachable.PinAttachable;
import com.chyzman.chowl.oddities.attachable.StickyNoteAttachable;
import com.chyzman.chowl.oddities.attachable.renderer.PinRenderer;
import com.chyzman.chowl.oddities.attachable.renderer.StickyNoteRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class OdditiesAttachables {

    public static final AttachableType<StickyNoteAttachable> STICKY_NOTE = register(
            "sticky_note",
            new AttachableType.Builder<>(
                    type -> new StickyNoteAttachable(),
                    StickyNoteAttachable.ENDEC
            )
    );

    public static final AttachableType<PinAttachable> PIN = register(
            "pin",
            new AttachableType.Builder<>(
                    type -> new PinAttachable(),
                    PinAttachable.ENDEC
            )
    );

    public static <T extends Attachable> AttachableType<T> register(String id, AttachableType.Builder<T> type) {
        return AttachableType.register(Oddities.id(id), type);
    }

    public static void init() {
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        AttachableRendererFactories.register(OdditiesAttachables.STICKY_NOTE, ctx -> new StickyNoteRenderer());
        AttachableRendererFactories.register(OdditiesAttachables.PIN, ctx -> new PinRenderer());
    }
}
