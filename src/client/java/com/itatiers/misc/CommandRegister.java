package com.itatiers.misc;

import com.itatiers.ItaTiersClient;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.concurrent.CompletableFuture;

public class CommandRegister {
    private static final SuggestionProvider<FabricClientCommandSource> PLAYERS = (_, builder) -> suggestPlayers(builder);

    private static CompletableFuture<Suggestions> suggestPlayers(SuggestionsBuilder builder) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.getConnection() == null)
            return builder.buildFuture();

        for (PlayerInfo entry : client.getConnection().getOnlinePlayers())
            if (SharedSuggestionProvider.matchesSubStr(builder.getRemaining().toLowerCase(), entry.getProfile().name().toLowerCase()) &&
                    entry.getProfile().name().length() > 2)
                builder.suggest(entry.getProfile().name(), () -> "Search tiers for " + entry.getProfile().name());

        if (SharedSuggestionProvider.matchesSubStr(builder.getRemaining().toLowerCase(), "-config"))
            builder.suggest("-config", () -> "Open the config screen");

        return builder.buildFuture();
    }

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, _) -> dispatcher.register(
                ClientCommands.literal("itatiers")
                        .executes(ItaTiersClient::toggleMod)
                        .then(ClientCommands.argument("Name", StringArgumentType.string())
                                .suggests(PLAYERS)
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "Name");
                                    return ItaTiersClient.searchPlayer(name);
                                })
                        )
        ));
    }
}