package com.chyzman.chowl.mixin.lavender;

import com.chyzman.chowl.Chowl;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.wispforest.lavender.book.Category;
import io.wispforest.lavender.book.LavenderBookItem;
import io.wispforest.lavender.client.LavenderBookScreen;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.UISounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;

@Mixin(LavenderBookScreen.LandingPageSupplier.class)
public abstract class LandingPageSupplierMixin extends LavenderBookScreen.PageSupplier {
    protected LandingPageSupplierMixin(LavenderBookScreen context) {
        super(context);
    }

    @ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Collection;isEmpty()Z"))
    private boolean malding(boolean original, LavenderBookScreen ctx) {
        return ctx.book.id().getNamespace().equals(Chowl.MODID) || original;
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lio/wispforest/lavender/client/LavenderBookScreen$LandingPageSupplier;buildEntryIndex(Ljava/util/Collection;[I)Ljava/util/List;"))
    private void explosion(LavenderBookScreen context, CallbackInfo ci) {
        if (!context.book.id().getNamespace().equals(Chowl.MODID)) return;

        var categoryContainer = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        var book = context.book;
        var client = MinecraftClient.getInstance();

        book
            .categories().stream()
            .sorted(Comparator.comparingInt(Category::ordinal))
            .sorted(Comparator.comparing($ -> !book.shouldDisplayCategory($, client.player)))
            .forEach(category -> {
//                if (book.shouldDisplayCategory(category, client.player)) {
//                    categoryContainer.child(Components.item(category.icon()).<ItemComponent>configure(categoryButton -> {
//                        categoryButton
//                            .tooltip(Text.literal(category.title()))
//                            .margins(Insets.of(4))
//                            .cursorStyle(CursorStyle.HAND);
//
//                        categoryButton.mouseDown().subscribe((mouseX, mouseY, button) -> {
//                            if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;
//
//                            context.navPush(new LavenderBookScreen.CategoryPageSupplier(context, category));
//                            UISounds.playInteractionSound();
//                            return true;
//                        });
//                    }));
//                } else if (!category.secret()) {
//                    categoryContainer.child(this.context.template(Component.class, "locked-category-button"));
//                }

                ParentComponent indexItem;
                if (book.shouldDisplayCategory(category, client.player)) {
                    indexItem = ((LavenderBookScreenAccessor) context).callTemplate(ParentComponent.class, "index-item");
                    indexItem.childById(ItemComponent.class, "icon").stack(category.icon());

                    var label = indexItem.childById(LabelComponent.class, "index-label");

                    label.text(Text.literal(category.title()).styled($ -> $.withFont(MinecraftClient.UNICODE_FONT_ID)));
                    label.mouseDown().subscribe((mouseX, mouseY, button) -> {
                        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

                        context.navPush(new LavenderBookScreen.CategoryPageSupplier(context, category));
                        UISounds.playInteractionSound();
                        return true;
                    });

                    var animation = label.color().animate(150, Easing.SINE, Color.ofFormatting(Formatting.GOLD));
                    label.mouseEnter().subscribe(animation::forwards);
                    label.mouseLeave().subscribe(animation::backwards);
                } else {
                    indexItem = ((LavenderBookScreenAccessor) context).callTemplate(ParentComponent.class, "locked-index-item");
                    indexItem.childById(LabelComponent.class, "index-label").text(Text.translatable("text.lavender.entry.locked"));
                }

                categoryContainer.child(indexItem);
            });

        var allEntriesItem = ((LavenderBookScreenAccessor) context).callTemplate(ParentComponent.class, "index-item");
        allEntriesItem.childById(ItemComponent.class, "icon").stack(LavenderBookItem.itemOf(book));

        var label = allEntriesItem.childById(LabelComponent.class, "index-label");

        label.text(Text.translatable("text.lavender.index_category").styled($ -> $.withFont(MinecraftClient.UNICODE_FONT_ID)));
        label.mouseDown().subscribe((mouseX, mouseY, button) -> {
            if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

            context.navPush(new LavenderBookScreen.IndexPageSupplier(context));
            UISounds.playInteractionSound();
            return true;
        });

        var animation = label.color().animate(150, Easing.SINE, Color.ofFormatting(Formatting.GOLD));
        label.mouseEnter().subscribe(animation::forwards);
        label.mouseLeave().subscribe(animation::backwards);

        categoryContainer.child(allEntriesItem);

        ((FlowLayout) pages.get(1)).child(categoryContainer);
    }
}
