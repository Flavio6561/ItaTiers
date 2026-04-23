package com.itatiers.misc;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.itatiers.ItaTiersClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class ConfigManager {
    private static Config config;
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("ItaTiers.json");

    private static class Config {
        boolean toggleMod = true;
        boolean showIcons = true;
        boolean isSeparatorAdaptive = true;
        boolean showFlag = true;
        int flagPosition = 0;
        ItaTiersClient.ModesTierDisplay displayMode;

        ItaTiersClient.DisplayStatus positionItaTiers;
        Modes activeItaTiersMode;

        boolean anonymousUserAgent;
    }

    public static void loadConfig() {
        Gson gson = new Gson();
        File configFile = CONFIG_PATH.toFile();
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                config = gson.fromJson(reader, Config.class);
                if (config == null)
                    restoreFromClient();
            } catch (JsonSyntaxException | IOException exception) {
                restoreFromClient();
            }
        } else
            restoreFromClient();

        ItaTiersClient.toggleMod = config.toggleMod;
        ItaTiersClient.showIcons = config.showIcons;
        ItaTiersClient.isSeparatorAdaptive = config.isSeparatorAdaptive;
        ItaTiersClient.showFlag = config.showFlag;
        if (config.flagPosition != 0 && config.flagPosition != 1 && config.flagPosition != 2)
            config.flagPosition = 0;
        ItaTiersClient.flagPosition = config.flagPosition;

        if (Arrays.stream(ItaTiersClient.ModesTierDisplay.values()).toList().contains(config.displayMode))
            ItaTiersClient.displayMode = config.displayMode;

        if (Arrays.stream(ItaTiersClient.DisplayStatus.values()).toList().contains(config.positionItaTiers))
            ItaTiersClient.positionItaTiers = config.positionItaTiers;
        if (Arrays.stream(Modes.values()).toList().contains(config.activeItaTiersMode))
            ItaTiersClient.activeItaTiersMode = config.activeItaTiersMode;

        ItaTiersClient.anonymousUserAgent = config.anonymousUserAgent;

        saveConfig();
    }

    private static void restoreFromClient() {
        config = new Config();

        config.toggleMod = ItaTiersClient.toggleMod;
        config.showIcons = ItaTiersClient.showIcons;
        config.isSeparatorAdaptive = ItaTiersClient.isSeparatorAdaptive;
        config.showFlag = ItaTiersClient.showFlag;

        if (ItaTiersClient.flagPosition != 0 && ItaTiersClient.flagPosition != 1 && ItaTiersClient.flagPosition != 2)
            ItaTiersClient.flagPosition = 0;
        config.flagPosition = ItaTiersClient.flagPosition;
        config.displayMode = ItaTiersClient.displayMode;

        config.positionItaTiers = ItaTiersClient.positionItaTiers;
        config.activeItaTiersMode = ItaTiersClient.activeItaTiersMode;

        config.anonymousUserAgent = ItaTiersClient.anonymousUserAgent;

        saveConfig();
    }

    public static void saveConfig() {
        Gson gson = new Gson();
        File configFile = CONFIG_PATH.toFile();
        Config currentConfig = new Config();

        currentConfig.toggleMod = ItaTiersClient.toggleMod;
        currentConfig.showIcons = ItaTiersClient.showIcons;
        currentConfig.isSeparatorAdaptive = ItaTiersClient.isSeparatorAdaptive;
        currentConfig.showFlag = ItaTiersClient.showFlag;

        if (ItaTiersClient.flagPosition != 0 && ItaTiersClient.flagPosition != 1 && ItaTiersClient.flagPosition != 2)
            ItaTiersClient.flagPosition = 0;
        currentConfig.flagPosition = ItaTiersClient.flagPosition;
        currentConfig.displayMode = ItaTiersClient.displayMode;

        currentConfig.positionItaTiers = ItaTiersClient.positionItaTiers;
        currentConfig.activeItaTiersMode = ItaTiersClient.activeItaTiersMode;

        currentConfig.anonymousUserAgent = ItaTiersClient.anonymousUserAgent;

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(currentConfig, writer);
        } catch (IOException exception) {
            restoreFromClient();
        }
    }
}