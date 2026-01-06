package com.chyzman.chowl.core.multipart.api.client.render.state;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.core.BlockPos;

public class PartRenderState {
    public BlockPos pos = BlockPos.ZERO;
    public List<? extends PartRenderState> subParts = Collections.emptyList();
    public PartType<?> type = null; // TODO: Use a default type instead of null
    @Nullable
    public ModelFeatureRenderer.CrumblingOverlay crumblingOverlay;

    public static void updatePartRenderState(Part part, PartRenderState state, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        state.pos = part.getPos();
        state.type = part.getType();
        state.crumblingOverlay = crumblingOverlay;
    }

    public void populateCrashReport(CrashReportCategory crashReportSection) {
        crashReportSection.setDetail("Part render state class", this.getClass().getCanonicalName());
        crashReportSection.setDetail("Position", this.pos);
        crashReportSection.setDetail("Type", this.type);
        crashReportSection.setDetail("Sub parts", "%s %s".formatted(this.subParts.size(), Arrays.toString(this.subParts.toArray())));
    }
}
