package com.chyzman.chowl.classes;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.SectionHeader;
import io.wispforest.owo.config.annotation.Sync;

import static com.chyzman.chowl.Chowl.MODID;

@Modmenu(modId = MODID)
@Config(name = "chowl-industries", wrapperName = "ChowlIndustriesConfig")
public class ChowlIndustriesConfigModel {

    @SectionHeader("Client")

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean has_chyz_been_reminded_to_add_more_config = false;

    @Sync(Option.SyncMode.NONE)
    public long max_capacity_level_before_exponents = 64L;

    @SectionHeader("Server")

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public String base_panel_capacity = "2048";

}