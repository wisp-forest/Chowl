package com.chyzman.chowl.core.temporaryReimplementationOfNestedLang.mixin;

import com.chyzman.chowl.core.temporaryReimplementationOfNestedLang.NestedLangHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Set;

@Mixin(Language.class)
public class LanguageMixin {
    @Unique private static boolean skipNext;

    @WrapOperation(method = "load(Ljava/io/InputStream;Ljava/util/function/BiConsumer;)V", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;entrySet()Ljava/util/Set;"))
    private static Set<Map.Entry<String, JsonElement>> deNestNestedKeys(JsonObject instance, Operation<Set<Map.Entry<String, JsonElement>>> original) {
        var key = "owo:disable_nested_lang";
        if (instance.has(key) && instance.get(key).getAsBoolean()) {
            instance.remove(key);
            return original.call(instance);
        }
        return NestedLangHandler.deNest(original.call(instance));
    }
}
