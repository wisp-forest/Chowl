package com.chyzman.chowl.pond.lavender;

import io.wispforest.lavender.client.LavenderBookScreen;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public interface EntryAccess {
    void chowl$setFake(Text realText, Supplier<LavenderBookScreen.PageSupplier> realPage);
    boolean chowl$isFake();
    Text chowl$realText();
    Supplier<LavenderBookScreen.PageSupplier> chowl$realPage();
}
