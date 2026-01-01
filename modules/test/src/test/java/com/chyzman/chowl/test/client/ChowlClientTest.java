package com.chyzman.chowl.test.client;

import com.chyzman.chowl.core.multipart.api.client.PartRendererFactories;
import com.chyzman.chowl.test.client.render.FramePanelRenderer;
import com.chyzman.chowl.test.client.render.TestPartRenderer;
import com.chyzman.chowl.test.registry.TestParts;
import net.fabricmc.api.ClientModInitializer;

public class ChowlClientTest implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PartRendererFactories.register(TestParts.TEST_PART, TestPartRenderer::new);
        PartRendererFactories.register(TestParts.FRAME_PANEL_PART, FramePanelRenderer::new);
    }
}
