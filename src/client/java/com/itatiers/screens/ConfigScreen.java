package com.itatiers.screens;

import com.itatiers.ItaTiersClient;
import com.itatiers.profile.PlayerProfile;
import com.itatiers.profile.Status;
import com.itatiers.textures.ColorControl;
import com.itatiers.textures.Icons;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ConfigScreen extends Screen {
    public static PlayerProfile ownProfile;
    public static PlayerProfile defaultProfile;

    private boolean useOwnProfile = false;

    private Identifier playerAvatarTexture;
    private boolean imageReady = false;

    private Button toggleModWidget;
    private Button toggleShowIcons;
    private Button toggleSeparatorMode;
    private Button cycleDisplayMode;
    private Button clearPlayerCache;
    private Button enableOwnProfile;
    private Button positionItaTiers;
    public Button toggleFlag;
    public Button cycleFlagPosition;

    private Button activeMode;

    private int distance;

    private ConfigScreen() {
        super(Component.literal("ItaTiers config"));
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        int centerX = width / 2;
        distance = height / 14;

        super.extractRenderState(context, mouseX, mouseY, delta);

        context.centeredText(font, Component.literal("ItaTiers config"), centerX, height / 50, ColorControl.getColorMinecraftStandard("text"));

        drawPlayerAvatar(context, centerX, height - 10 - (int) (width / 6.666));
        context.centeredText(font, ItaTiersClient.getNametag(useOwnProfile ? ownProfile : defaultProfile), centerX, height - 24 - (int) (width / 6.666), ColorControl.getColorMinecraftStandard("text"));

        context.text(font, ItaTiersClient.getActiveIcon(), centerX + 62, distance + 75 + 9, ColorControl.getColorMinecraftStandard("text"));

        checkUpdates();
    }

    private void checkUpdates() {
        toggleModWidget.setPosition(width / 2 - 88 - 2, distance);
        toggleShowIcons.setPosition(width / 2 + 2, distance);
        toggleSeparatorMode.setPosition(width / 2 - 90, distance + 25);
        cycleDisplayMode.setPosition(width / 2 - 90, distance + 50);
        positionItaTiers.setPosition(width / 2 - 88 - 2, distance + 75);
        toggleFlag.setPosition(width / 2 - 88 - 2 + 28 + 2, distance + 75);
        cycleFlagPosition.setPosition(width / 2 - 88 - 2 + 28 + 2 + 28 + 2, distance + 75);
        activeMode.setPosition(width / 2 + 2, distance + 75);
        enableOwnProfile.setPosition(width / 2 - 90, distance + 100);

        clearPlayerCache.setPosition(width - 88 - 5, height - 20 - 5);
    }

    @Override
    protected void init() {
        toggleModWidget = Button.builder(Component.literal(ItaTiersClient.toggleMod ? "Disable ItaTiers" : "Enable ItaTiers").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (Button) -> {
            ItaTiersClient.toggleMod();
            Button.setMessage(Component.literal(ItaTiersClient.toggleMod ? "Disable ItaTiers" : "Enable ItaTiers").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
            toggleModWidget.setTooltip(Tooltip.create(Component.literal(ItaTiersClient.toggleMod ? "Disable the mod" : "Enable the mod")));
        }).bounds(width / 2 - 88 - 2, distance, 88, 20).build();
        toggleModWidget.setTooltip(Tooltip.create(Component.literal(ItaTiersClient.toggleMod ? "Disable the mod" : "Enable the mod")));

        toggleShowIcons = Button.builder(Component.literal(ItaTiersClient.showIcons ? "Disable Icons" : "Enable Icons").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (Button) -> {
            ItaTiersClient.toggleShowIcons();
            Button.setMessage(Component.literal(ItaTiersClient.showIcons ? "Disable Icons" : "Enable Icons").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
            toggleShowIcons.setTooltip(Tooltip.create(Component.literal(ItaTiersClient.showIcons ? "Disable the gamemode icon next to the tier" : "Enable the gamemode icon next to the tier")));
        }).bounds(width / 2 + 2, distance, 88, 20).build();
        toggleShowIcons.setTooltip(Tooltip.create(Component.literal(ItaTiersClient.showIcons ? "Disable the gamemode icon next to the tier" : "Enable the gamemode icon next to the tier")));

        toggleSeparatorMode = Button.builder(Component.literal(ItaTiersClient.isSeparatorAdaptive ? "Disable Dynamic Separator" : "Enable Dynamic Separator").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (Button) -> {
            ItaTiersClient.toggleSeparatorAdaptive();
            Button.setMessage(Component.literal(ItaTiersClient.isSeparatorAdaptive ? "Disable Dynamic Separator" : "Enable Dynamic Separator").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
            toggleSeparatorMode.setTooltip(Tooltip.create(Component.literal(ItaTiersClient.isSeparatorAdaptive ? "Make the Tiers separator gray" : "Make the Tiers separator match the tier color")));
        }).bounds(width / 2 - 90, distance + 25, 180, 20).build();
        toggleSeparatorMode.setTooltip(Tooltip.create(Component.literal(ItaTiersClient.isSeparatorAdaptive ? "Make the Tiers separator gray" : "Make the Tiers separator match the tier color")));

        cycleDisplayMode = Button.builder(Component.literal(ItaTiersClient.displayMode.getCurrentMode()).setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (Button) -> {
            ItaTiersClient.cycleDisplayMode();
            Button.setMessage(Component.literal(ItaTiersClient.displayMode.getCurrentMode()).setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
        }).bounds(width / 2 - 90, distance + 50, 180, 20).build();
        cycleDisplayMode.setTooltip(Tooltip.create(Component.literal("""
                Selected: only the selected tier will be displayed
                
                Highest: only the highest tier will be displayed
                
                Adaptive Highest: the highest tier will be displayed if selected does not exist""")));

        positionItaTiers = Button.builder(Component.literal(ItaTiersClient.positionItaTiers.getStatus()), (Button) -> {
            ItaTiersClient.cycleItaTiersPosition();
            Button.setMessage(Component.literal(ItaTiersClient.positionItaTiers.getStatus()));
        }).bounds(width / 2 - 88 - 2, distance + 75, 28, 20).build();

        positionItaTiers.setTooltip(Tooltip.create(Component.literal("""
                Right: Tiers will be displayed on the right of the nametag
                
                Left: Tiers will be displayed on the left of the nametag""")));

        activeMode = Button.builder(Icons.CYCLE, (_) -> ItaTiersClient.cycleModes()).bounds(width / 2 + 2, distance + 75, 44, 20).build();
        activeMode.setTooltip(Tooltip.create(Component.literal("Cycle active gamemode")));

        toggleFlag = Button.builder(ItaTiersClient.showFlag ? Icons.IT_FLAG_BUTTON: Icons.NO_IT_FLAG_BUTTON, (Button) -> {
            ItaTiersClient.toggleFlag();
            cycleFlagPosition.active = ItaTiersClient.showFlag;
            Button.setMessage(ItaTiersClient.showFlag ? Icons.IT_FLAG_BUTTON: Icons.NO_IT_FLAG_BUTTON);
            toggleFlag.setTooltip(Tooltip.create(Component.literal(ItaTiersClient.showFlag ? "Disable the flag in the nametag" : "Enable the flag in the nametag")));
        }).bounds(width / 2 - 88 - 2 + 28 + 2, distance + 75, 28, 20).build();

        cycleFlagPosition = Button.builder(Icons.CYCLE, (_) -> {
            ItaTiersClient.cycleFlagPosition();
            cycleFlagPosition.setTooltip(Tooltip.create(Component.literal("Cycle flag position in the nametag")));
        }).bounds(width / 2 - 88 - 2 + 28 + 2 + 28 + 2, distance + 75, 28, 20).build();

        cycleFlagPosition.active = ItaTiersClient.showFlag;

        if (ownProfile.status == Status.READY) {
            enableOwnProfile = Button.builder(Component.literal(useOwnProfile ? "Preview default" : "Preview " + ownProfile.name).setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (Button) -> {
                useOwnProfile = !useOwnProfile;

                loadPlayerAvatar();

                Button.setMessage(Component.literal(useOwnProfile ? "Preview default" : "Preview " + ownProfile.name).setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
                enableOwnProfile.setTooltip(Tooltip.create(Component.literal(useOwnProfile ? "Preview the default profile (" + defaultProfile.name + ")" : "Preview your player profile (" + ownProfile.name + ")")));
            }).bounds(width / 2 - 90, distance + 100, 180, 20).build();
            enableOwnProfile.setTooltip(Tooltip.create(Component.literal(useOwnProfile ? "Preview the default profile (" + defaultProfile.name + ")" : "Preview your player profile (" + ownProfile.name + ")")));
        } else {
            enableOwnProfile = Button.builder(Component.literal("Cannot switch profiles").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (_) -> {
            }).bounds(width / 2 - 90, distance + 100, 180, 20).build();
            enableOwnProfile.setTooltip(Tooltip.create(Component.literal("Can't switch profiles: " + ownProfile.name + " is not found or fetched yet")));
        }

        clearPlayerCache = Button.builder(Component.literal("Clear cache").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (_) -> ItaTiersClient.clearCache(false)).bounds(width - 88 - 10, height - 20 - 10, 88, 20).build();
        clearPlayerCache.setTooltip(Tooltip.create(Component.literal("Clear all player cache")));

        addRenderableWidget(toggleModWidget);
        addRenderableWidget(toggleShowIcons);
        addRenderableWidget(toggleSeparatorMode);
        addRenderableWidget(cycleDisplayMode);
        addRenderableWidget(positionItaTiers);
        addRenderableWidget(toggleFlag);
        addRenderableWidget(cycleFlagPosition);
        addRenderableWidget(activeMode);
        addRenderableWidget(enableOwnProfile);
        addRenderableWidget(clearPlayerCache);
    }

    private void drawPlayerAvatar(GuiGraphicsExtractor context, int x, int y) {
        if (playerAvatarTexture != null && imageReady)
            context.blit(RenderPipelines.GUI_TEXTURED, playerAvatarTexture, x - width / 32, y, 0, 0, width / 16, (int) (width / 6.666), width / 16, (int) (width / 6.666));
        else if (ownProfile.numberOfImageRequests > 4)
            context.centeredText(font, Component.literal(ownProfile.name + "'s skin failed to load. Clear cache and retry"), x, y + 40, ColorControl.getColorMinecraftStandard("red"));
        else
            loadPlayerAvatar();
    }

    private void loadPlayerAvatar() {
        File avatarFile = FabricLoader.getInstance().getGameDir().resolve("cache/itatiers/" + (useOwnProfile ? ownProfile.uuid : defaultProfile.uuid) + ".png").toFile();
        if (!avatarFile.exists())
            return;

        try (FileInputStream stream = new FileInputStream(avatarFile)) {
            playerAvatarTexture = Identifier.fromNamespaceAndPath("itatiers", (useOwnProfile ? ownProfile.uuid : defaultProfile.uuid));
            Minecraft.getInstance().getTextureManager().register(playerAvatarTexture, new DynamicTexture(String::new, NativeImage.read(stream)));
            imageReady = true;
        } catch (IOException ignored) {
        }
    }

    public static Screen getConfigScreen(Screen ignoredScreen) {
        return new ConfigScreen();
    }
}