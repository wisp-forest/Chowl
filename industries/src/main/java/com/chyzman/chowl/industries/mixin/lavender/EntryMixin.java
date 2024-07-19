package com.chyzman.chowl.industries.mixin.lavender;

import com.chyzman.chowl.industries.pond.lavender.EntryAccess;
import io.wispforest.lavender.book.Entry;
import io.wispforest.lavender.client.LavenderBookScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Mixin(Entry.class)
public class EntryMixin implements EntryAccess {
    @Unique private boolean chowl$isFake = false;
    @Unique private Text chowl$realText = null;
    @Unique private Supplier<LavenderBookScreen.PageSupplier> chowl$realPage = null;

    @Override
    public void chowl$setFake(Text realText, Supplier<LavenderBookScreen.PageSupplier> realPage) {
        chowl$isFake = true;
        chowl$realText = realText;
        chowl$realPage = realPage;
    }

    @Override
    public boolean chowl$isFake() {
        return chowl$isFake;
    }

    @Override
    public Text chowl$realText() {
        return chowl$realText;
    }

    @Override
    public Supplier<LavenderBookScreen.PageSupplier> chowl$realPage() {
        return chowl$realPage;
    }
}
