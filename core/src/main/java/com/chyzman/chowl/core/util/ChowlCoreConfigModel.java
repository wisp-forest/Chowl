package com.chyzman.chowl.core.util;

import com.chyzman.chowl.core.ChowlCore;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.SectionHeader;
import io.wispforest.owo.config.annotation.Sync;

@Modmenu(modId = ChowlCore.MODID)
@Config(name = "chowl-core", wrapperName = "ChowlCoreConfig")
public class ChowlCoreConfigModel {

    @SectionHeader("Client")

    @Sync(Option.SyncMode.NONE)
    public int max_ticks_for_double_click = 5;

    @Sync(Option.SyncMode.NONE)
    public int recursive_rendering_limit = 3;

}