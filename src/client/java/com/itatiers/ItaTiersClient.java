package com.itatiers;

import com.mojang.brigadier.context.CommandContext;
import com.itatiers.misc.*;
import com.itatiers.profile.GameMode;
import com.itatiers.profile.PlayerProfile;
import com.itatiers.profile.Status;
import com.itatiers.profile.types.SuperProfile;
import com.itatiers.screens.ConfigScreen;
import com.itatiers.screens.PlayerSearchResultScreen;
import com.itatiers.textures.ColorControl;
import com.itatiers.textures.ColorLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ItaTiersClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(ItaTiersClient.class);
    public static String userAgent = "ItaTiers (https://github.com/Flavio6561/ItaTiers)";
    public static boolean anonymousUserAgent = false;
    private static final ArrayList<PlayerProfile> playerProfiles = new ArrayList<>();
    private static final HashMap<String, Text> playerTexts = new HashMap<>();

    public static boolean toggleMod = true;
    public static boolean showIcons = true;
    public static boolean isSeparatorAdaptive = true;
    public static ModesTierDisplay displayMode = ModesTierDisplay.ADAPTIVE_HIGHEST;

    public static DisplayStatus positionItaTiers = DisplayStatus.LEFT;
    public static Modes activeItaTiersMode = Modes.VANILLA;

    private static KeyBinding autoDetectKey;
    private static KeyBinding cycleKey;

    @Override
    public void onInitializeClient() {
        ConfigManager.loadConfig();
        clearCache(true);
        CommandRegister.registerCommands();

        Optional<ModContainer> fabricLoader = FabricLoader.getInstance().getModContainer("itatiers");

        fabricLoader.ifPresent(tiers -> {
            ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of("itatiers", "itatiers-default"), tiers, ResourcePackActivationType.ALWAYS_ENABLED);
            if (!anonymousUserAgent)
                userAgent += " " + fabricLoader.get().getMetadata().getVersion().getFriendlyString() + " on " + MinecraftClient.getInstance().getGameVersion();
        });

        autoDetectKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Auto Detect Kit", GLFW.GLFW_KEY_Y, "ItaTiers"));
        cycleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Cycle Gamemodes", GLFW.GLFW_KEY_U, "ItaTiers"));

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ColorLoader());
        ClientTickEvents.END_CLIENT_TICK.register(ItaTiersClient::tickUtils);

        LOGGER.info("ItaTiers initialized | User agent: {}", userAgent);
    }

    public static Text getModifiedNametag(String originalName, Text originalNameText) {
        PlayerProfile profile = addGetPlayer(originalName, false);
        if (profile.status == Status.READY)
            if (profile.originalNameText == null || profile.originalNameText != originalNameText)
                updatePlayerNametag(originalNameText, profile);

        if (playerTexts.containsKey(originalName)) return playerTexts.get(originalName);

        return originalNameText;
    }

    public static Text getNametag(PlayerProfile profile) {
        if (!toggleMod || profile.status != Status.READY) return profile.originalNameText;

        Text rightText = Text.literal("");
        Text leftText = Text.literal("");
        Text nameText = Text.of(profile.name);

        if (positionItaTiers == DisplayStatus.RIGHT)
            rightText = updateProfileNameTagRight(profile.profileItaTiers, activeItaTiersMode);
        else if (positionItaTiers == DisplayStatus.LEFT)
            leftText = updateProfileNameTagLeft(profile.profileItaTiers, activeItaTiersMode);

        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.world == null || client.getNetworkHandler() == null) && profile.uuidObject != null && client.getNetworkHandler().getPlayerUuids().contains(profile.uuidObject))
            nameText = profile.originalNameText;

        return Text.literal("")
                .append(leftText)
                .append(nameText)
                .append(rightText);
    }

    private static void updateAllTags() {
        for (PlayerProfile profile : playerProfiles)
            if (profile.status == Status.READY && profile.originalNameText != null)
                updatePlayerNametag(profile.originalNameText, profile);

        if (ConfigScreen.ownProfile.status == Status.READY)
            updatePlayerNametag(ConfigScreen.ownProfile.originalNameText, ConfigScreen.ownProfile);
        updatePlayerNametag(ConfigScreen.defaultProfile.originalNameText, ConfigScreen.defaultProfile);
    }

    private static void updatePlayerNametag(Text originalNameText, PlayerProfile profile) {
        Text rightText = Text.literal("");
        Text leftText = Text.literal("");

        if (positionItaTiers == DisplayStatus.RIGHT)
            rightText = updateProfileNameTagRight(profile.profileItaTiers, activeItaTiersMode);
        else if (positionItaTiers == DisplayStatus.LEFT)
            leftText = updateProfileNameTagLeft(profile.profileItaTiers, activeItaTiersMode);

        playerTexts.put(profile.name, Text.literal("")
                .append(leftText)
                .append(originalNameText)
                .append(rightText));

        profile.originalNameText = originalNameText;
    }

    private static Text updateProfileNameTagRight(SuperProfile profile, Modes activeMode) {
        MutableText returnValue = Text.literal("");
        if (profile.status == Status.READY) {
            GameMode shown = profile.getGameMode(activeMode);

            if ((shown == null || shown.status == Status.SEARCHING) || (shown.status == Status.NOT_EXISTING && displayMode == ModesTierDisplay.SELECTED))
                return returnValue;

            if (displayMode == ModesTierDisplay.ADAPTIVE_HIGHEST && shown.status == Status.NOT_EXISTING && profile.highest != null)
                shown = profile.highest;

            if (displayMode == ModesTierDisplay.HIGHEST && profile.highest != null && profile.highest.getTierPoints() > shown.getTierPoints())
                shown = profile.highest;

            if (shown == null || shown.status != Status.READY)
                return returnValue;

            MutableText separator = Text.literal(" | ").setStyle(isSeparatorAdaptive ? shown.displayedTier.getStyle() : Style.EMPTY.withColor(ColorControl.getColor("static_separator")));
            returnValue.append(Text.literal("").append(separator).append(shown.displayedTier));

            if (showIcons)
                returnValue.append(Text.literal(" ").append(shown.name.iconTag));
        }
        return returnValue;
    }

    private static Text updateProfileNameTagLeft(SuperProfile profile, Modes activeMode) {
        MutableText returnValue = Text.literal("");
        if (profile.status == Status.READY) {
            GameMode shown = profile.getGameMode(activeMode);

            if ((shown == null || shown.status == Status.SEARCHING) || (shown.status == Status.NOT_EXISTING && displayMode == ModesTierDisplay.SELECTED))
                return returnValue;

            if (displayMode == ModesTierDisplay.ADAPTIVE_HIGHEST && shown.status == Status.NOT_EXISTING && profile.highest != null)
                shown = profile.highest;

            if (displayMode == ModesTierDisplay.HIGHEST && profile.highest != null && profile.highest.getTierPoints() > shown.getTierPoints())
                shown = profile.highest;

            if (shown == null || shown.status != Status.READY)
                return returnValue;

            MutableText separator = Text.literal(" | ").setStyle(isSeparatorAdaptive ? shown.displayedTier.getStyle() : Style.EMPTY.withColor(ColorControl.getColor("static_separator")));

            if (showIcons)
                returnValue = Text.literal("").append(shown.name.iconTag).append(" ");
            returnValue.append(Text.literal("").append(shown.displayedTier).append(separator));
        }
        return returnValue;
    }

    public static void restyleAllTexts() {
        for (PlayerProfile profile : playerProfiles) {
            if (profile.status == Status.READY) {
                if (profile.profileItaTiers.status == Status.READY)
                    profile.profileItaTiers.parseJson(profile.profileItaTiers.originalJson);
            }
        }
    }

    private static void tickUtils(MinecraftClient client) {
        if (!client.isFinishedLoading()) return;

        if (ConfigScreen.defaultProfile == null) {
            ConfigScreen.ownProfile = new PlayerProfile(client.getGameProfile().getName(), false);
            PlayerProfileQueue.enqueue(ConfigScreen.ownProfile);

            ConfigScreen.defaultProfile = new PlayerProfile("{\"id\":\"da300ba3690b43228feacf1628825c88\",\"name\":\"Sbiguss\"}",
                    "{\"uuid\":\"da300ba3690b43228feacf1628825c88\",\"username\":\"Sbiguss\",\"retired\":0,\"tiers\":{\"elytra\":\"HT3\",\"uhc\":\"LT2\",\"nethpot\":\"HT3\",\"vanilla\":\"LT4\",\"axe\":\"HT2\",\"smp\":\"HT2\",\"diapot\":\"HT2\",\"sword\":\"HT2\"},\"points\":151}");
        }

        if (autoDetectKey.wasPressed())
            InventoryChecker.checkInventory(client);

        if (cycleKey.wasPressed()) {
            Text message = cycleModes();

            sendMessageToPlayer(message, true);
        }
    }

    public static Text cycleModes() {
        return Text.literal("ItaTiers is now displaying ").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))).append(cycleItaTiersMode());
    }

    public static Text getActiveIcon() {
        return activeItaTiersMode.icon;
    }

    public static void sendMessageToPlayer(String message, int color, boolean overlay) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null)
            client.player.sendMessage((Text.literal(message).setStyle(Style.EMPTY.withColor(color))), overlay);
    }

    public static void sendMessageToPlayer(Text message, boolean overlay) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null)
            client.player.sendMessage(message, overlay);
    }

    public static int toggleMod(CommandContext<FabricClientCommandSource> ignoredFabricClientCommandSourceCommandContext) {
        toggleMod = !toggleMod;
        ConfigManager.saveConfig();
        sendMessageToPlayer("ItaTiers is now " + (toggleMod ? "enabled" : "disabled"), (toggleMod ? ColorControl.getColor("green") : ColorControl.getColor("red")), true);
        return 1;
    }

    public static void toggleMod() {
        toggleMod = !toggleMod;
        ConfigManager.saveConfig();
    }

    private static PlayerProfile addGetPlayer(String name, boolean priority) {
        for (PlayerProfile profile : playerProfiles) {
            if (profile.name.equalsIgnoreCase(name)) {
                if (priority)
                    PlayerProfileQueue.changeToFirstInQueue(profile);
                return profile;
            }
        }
        PlayerProfile newProfile = new PlayerProfile(name, true);

        if (priority)
            PlayerProfileQueue.putFirstInQueue(newProfile);
        else
            PlayerProfileQueue.enqueue(newProfile);

        playerProfiles.add(newProfile);
        return newProfile;
    }

    private static void openPlayerSearchResultScreen(PlayerProfile profile) {
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new PlayerSearchResultScreen(profile)));
    }

    private static void openConfigScreen() {
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(ConfigScreen.getConfigScreen(null)));
    }

    public static int searchPlayer(String name) {
        if (name.equalsIgnoreCase("toggle"))
            toggleMod(null);
        else if (name.equalsIgnoreCase("config"))
            CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS).execute(ItaTiersClient::openConfigScreen);
        else
            CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS).execute(() -> openPlayerSearchResultScreen(addGetPlayer(name, true)));
        return 1;
    }

    public static void clearCache(boolean start) {
        playerProfiles.clear();
        playerTexts.clear();
        PlayerProfileQueue.clearQueue();
        try {
            FileUtils.deleteDirectory(new File(FabricLoader.getInstance().getGameDir() + (start ? "/cache/itatiers" : "/cache/itatiers/players")));
        } catch (IOException e) {
            LOGGER.warn("Error deleting cache folder: {}", e.getMessage());
        }
    }

    public static void toggleSeparatorAdaptive() {
        isSeparatorAdaptive = !isSeparatorAdaptive;
        updateAllTags();
        ConfigManager.saveConfig();
    }

    public static void toggleShowIcons() {
        showIcons = !showIcons;
        updateAllTags();
        ConfigManager.saveConfig();
    }

    public static Text cycleItaTiersMode() {
        activeItaTiersMode = cycleEnum(activeItaTiersMode, Modes.getValues());
        updateAllTags();
        ConfigManager.saveConfig();
        return activeItaTiersMode.label;
    }

    public static void cycleItaTiersPosition() {
        positionItaTiers = cycleEnum(positionItaTiers, DisplayStatus.values());
        updateAllTags();
        ConfigManager.saveConfig();
    }

    public static void cycleDisplayMode() {
        displayMode = cycleEnum(displayMode, ModesTierDisplay.values());
        updateAllTags();
        ConfigManager.saveConfig();
    }

    private static <T extends Enum<T>> T cycleEnum(T current, T[] values) {
        return values[(current.ordinal() + 1) % values.length];
    }

    public enum ModesTierDisplay {
        HIGHEST,
        SELECTED,
        ADAPTIVE_HIGHEST;

        public String getCurrentMode() {
            if (this.toString().equalsIgnoreCase("HIGHEST"))
                return "Displayed Tiers: Highest";
            else if (this.toString().equalsIgnoreCase("SELECTED"))
                return "Displayed Tiers: Selected";
            return "Displayed Tiers: Adaptive Highest";
        }
    }

    public enum DisplayStatus {
        RIGHT,
        LEFT;

        public String getStatus() {
            if (this.toString().equalsIgnoreCase("RIGHT"))
                return "Right";
            return "Left";
        }
    }
}
