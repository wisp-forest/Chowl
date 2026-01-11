package com.chyzman.chowl.core.panel.registry;

import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.panel.part.PanelPart;
import com.chyzman.chowl.core.registry.CoreParts;

public class PanelParts {

    public static final PartType<PanelPart> PANEL = CoreParts.register("panel", new PartType<>(PanelPart.ENDEC, PanelPart::new));

    public static void init() {}
}
