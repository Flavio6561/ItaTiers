package com.itatiers.textures;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.itatiers.misc.Modes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.itatiers.ItaTiersClient.LOGGER;
import static com.itatiers.ItaTiersClient.restyleAllTexts;

public class ColorLoader implements PreparableReloadListener {
    public static Identifier identifier = Identifier.fromNamespaceAndPath("minecraft", "colors/ita_colors.json");

    @Override
    public @NonNull CompletableFuture<Void> reload(SharedState currentReload, @NonNull Executor taskExecutor, @NonNull PreparationBarrier preparationBarrier, @NonNull Executor reloadExecutor) {
        if (currentReload.resourceManager().getResource(identifier).isPresent()) {
            try {
                ColorControl.updateColors(GsonHelper.fromJson(new Gson(), new InputStreamReader(currentReload.resourceManager().getResource(identifier).get().open(), StandardCharsets.UTF_8), JsonObject.class));
                Modes.updateColors();
                restyleAllTexts();
            } catch (IOException ignored) {
                LOGGER.warn("Error loading colors info");
            }
        }

        return CompletableFuture.runAsync(() -> {
        }, taskExecutor).thenCompose(preparationBarrier::wait).thenRunAsync(() -> {
        }, reloadExecutor);
    }
}