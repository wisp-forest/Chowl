package com.chyzman.chowl.industries.classes;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

import static com.chyzman.chowl.industries.Chowl.MODID;

@Modmenu(modId = MODID)
@Config(name = "chowl-industries", wrapperName = "ChowlIndustriesConfig")
public class ChowlIndustriesConfigModel {

    @SectionHeader("Client")

    @Sync(Option.SyncMode.NONE)
    public AbbreviationMode abbreviation_mode = AbbreviationMode.EXPONENTS;

    @Sync(Option.SyncMode.NONE)
    public int digits_before_abbreviation = 16;

    public enum AbbreviationMode {
        LETTERS,
        EXPONENTS,
        SCIENTIFIC,
        NONE
    }

    @Sync(Option.SyncMode.NONE)
    public boolean remove_panels_when_empty = false;

    @Sync(Option.SyncMode.NONE)
    public boolean cringe_ahh_book = false;

    @SectionHeader("Server")

    @Nest
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public BaseCapacityConfigs base_capacity = new BaseCapacityConfigs();

    public static class BaseCapacityConfigs {
        public String drawer = "2048";
        public String compressing = "2048";
        public String packing = "2048";
    }

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean double_click_templating = true;

}
