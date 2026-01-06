package com.chyzman.chowl.core.multipart.api.client.render.state;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PartRenderState {
    public BlockPos pos = BlockPos.ORIGIN;
    public List<? extends PartRenderState> subParts = Collections.emptyList();
    public PartType<?> type = null; // TODO: Use a default type instead of null
    @Nullable
    public ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay;

    public static void updatePartRenderState(Part part, PartRenderState state, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        state.pos = part.getPos();
        state.type = part.getType();
        state.crumblingOverlay = crumblingOverlay;
    }

    public void populateCrashReport(CrashReportSection crashReportSection) {
        crashReportSection.add("Part render state class", this.getClass().getCanonicalName());
        crashReportSection.add("Position", this.pos);
        crashReportSection.add("Type", this.type);
        crashReportSection.add("Sub parts", "%s %s".formatted(this.subParts.size(), Arrays.toString(this.subParts.toArray())));
    }
}
