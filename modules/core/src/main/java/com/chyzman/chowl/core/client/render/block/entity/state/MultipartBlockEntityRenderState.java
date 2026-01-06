package com.chyzman.chowl.core.client.render.block.entity.state;

import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;

import java.util.List;

public class MultipartBlockEntityRenderState extends BlockEntityRenderState {
    public List<? extends PartRenderState> partsToRender;
}
