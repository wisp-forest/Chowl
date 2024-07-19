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

    @Sync(Option.SyncMode.NONE)
    public String max_digits_before_exponents = "16";

    @Sync(Option.SyncMode.NONE)
    public int max_ticks_for_double_click = 5;

    @Sync(Option.SyncMode.NONE)
    public int recursive_rendering_limit = 3;

    @Sync(Option.SyncMode.NONE)
    public boolean cringe_ahh_book = false;

    @SectionHeader("Server")

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public String base_panel_capacity = "2048";
    public String base_compressing_panel_capacity = "2048";
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean double_click_templating = true;

}