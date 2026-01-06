package com.chyzman.chowl.core.client.render.block.entity.state;

import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import java.util.List;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class MultipartBlockEntityRenderState extends BlockEntityRenderState {
    public List<? extends PartRenderState> partsToRender;
}
