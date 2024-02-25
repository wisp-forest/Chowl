package com.chyzman.chowl.mixin.lavender;

import com.chyzman.chowl.Chowl;
import com.chyzman.chowl.pond.lavender.EntryAccess;
import com.google.common.collect.ImmutableSet;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.wispforest.lavender.book.Category;
import io.wispforest.lavender.book.Entry;
import io.wispforest.lavender.book.LavenderBookItem;
import io.wispforest.lavender.client.LavenderBookScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(value = LavenderBookScreen.LandingPageSupplier.class, remap = false)
public abstract class LandingPageSupplierMixin extends LavenderBookScreen.PageSupplier {
    protected LandingPageSupplierMixin(LavenderBookScreen context) {
        super(context);
    }

    private static boolean shouldEnableChowl(LavenderBookScreen ctx) {
        return ctx.book.id().getNamespace().equals(Chowl.MODID) && !Chowl.CHOWL_CONFIG.cringe_ahh_book();
    }

    @ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Collection;isEmpty()Z"))
    private boolean malding(boolean original, LavenderBookScreen ctx) {
        return shouldEnableChowl(ctx) || original;
    }

    @ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE", target = "Lio/wispforest/lavender/book/Book;orphanedEntries()Ljava/util/Collection;"))
    private Collection<Entry> malding(Collection<Entry> original, LavenderBookScreen context) {
        if (!shouldEnableChowl(context)) return original;

        List<Entry> better = new ArrayList<>(original);

        var book = context.book;
        var client = MinecraftClient.getInstance();

        for (Category category : book.categories()) {
            Entry basedEntry = new Entry(
                category.id(),
                null,
                category.title(),
                category.icon(),
                category.secret(),
                category.ordinal(),
                book.shouldDisplayCategory(category, client.player)
                    ? ImmutableSet.of()
                    : ImmutableSet.of(new Identifier("haha", "lmao")),
                ImmutableSet.of(),
                "explosion"
            );

            ((EntryAccess)(Object) basedEntry).chowl$setFake(null, () -> new LavenderBookScreen.CategoryPageSupplier(context, category));

            better.add(basedEntry);
        }

        var allEntries = new Entry(
            new Identifier("haha", "explosion"),
            null,
            "All Entries",
            LavenderBookItem.itemOf(context.book),
            false,
            100,
            ImmutableSet.of(),
            ImmutableSet.of(),
            "explosion"
        );

        ((EntryAccess)(Object) allEntries).chowl$setFake(
            Text.translatable("text.lavender.index_category").styled($ -> $.withFont(MinecraftClient.UNICODE_FONT_ID)),
            () -> new LavenderBookScreen.IndexPageSupplier(context)
        );

        better.add(allEntries);

        return better;
    }
}
