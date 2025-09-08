package com.itatiers.screens;

import com.itatiers.ItaTiersClient;
import com.itatiers.profile.Status;
import com.itatiers.textures.ColorControl;
import com.itatiers.textures.Icons;
import com.itatiers.profile.PlayerProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ConfigScreen extends Screen {
    public static PlayerProfile ownProfile;
    public static PlayerProfile defaultProfile;

    private boolean useOwnProfile = false;

    private Identifier playerAvatarTexture;
    private boolean imageReady = false;

    private ButtonWidget toggleModWidget;
    private ButtonWidget toggleShowIcons;
    private ButtonWidget toggleSeparatorMode;
    private ButtonWidget cycleDisplayMode;
    private ButtonWidget clearPlayerCache;
    private ButtonWidget enableOwnProfile;
    private ButtonWidget positionItaTiers;

    private ButtonWidget activeMode;

    private int distance;

    private ConfigScreen() {
        super(Text.literal("ItaTiers config"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int centerX = width / 2;
        distance = height / 14;

        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("ItaTiers config"), centerX, height / 50, ColorControl.getColorMinecraftStandard("text"));

        drawPlayerAvatar(context, centerX, height - 10 - (int) (width / 6.666));
        context.drawCenteredTextWithShadow(this.textRenderer, ItaTiersClient.getNametag(useOwnProfile ? ownProfile : defaultProfile), centerX, height - 24 - (int) (width / 6.666), ColorControl.getColorMinecraftStandard("text"));

        context.drawTextWithShadow(this.textRenderer, Text.of(ItaTiersClient.getActiveIcon()), centerX + 62, distance + 75 + 9, ColorControl.getColorMinecraftStandard("text"));

        checkUpdates();
    }

    private void checkUpdates() {
        toggleModWidget.setPosition(width / 2 - 88 - 2, distance);
        toggleShowIcons.setPosition(width / 2 + 2, distance);
        toggleSeparatorMode.setPosition(width / 2 - 90, distance + 25);
        cycleDisplayMode.setPosition(width / 2 - 90, distance + 50);
        positionItaTiers.setPosition(width / 2 - 88 - 2, distance + 75);
        activeMode.setPosition(width / 2 + 2, distance + 75);
        enableOwnProfile.setPosition(width / 2 - 90, distance + 100);

        clearPlayerCache.setPosition(width - 88 - 5, height - 20 - 5);
    }

    @Override
    protected void init() {
        toggleModWidget = ButtonWidget.builder(Text.literal(ItaTiersClient.toggleMod ? "Disable ItaTiers" : "Enable ItaTiers").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> {
            ItaTiersClient.toggleMod();
            buttonWidget.setMessage(Text.literal(ItaTiersClient.toggleMod ? "Disable ItaTiers" : "Enable ItaTiers").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
            toggleModWidget.setTooltip(Tooltip.of(Text.of(ItaTiersClient.toggleMod ? "Disable the mod" : "Enable the mod")));
        }).dimensions(width / 2 - 88 - 2, distance, 88, 20).build();
        toggleModWidget.setTooltip(Tooltip.of(Text.of(ItaTiersClient.toggleMod ? "Disable the mod" : "Enable the mod")));

        toggleShowIcons = ButtonWidget.builder(Text.literal(ItaTiersClient.showIcons ? "Disable Icons" : "Enable Icons").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> {
            ItaTiersClient.toggleShowIcons();
            buttonWidget.setMessage(Text.literal(ItaTiersClient.showIcons ? "Disable Icons" : "Enable Icons").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
            toggleShowIcons.setTooltip(Tooltip.of(Text.of(ItaTiersClient.showIcons ? "Disable the gamemode icon next to the tier" : "Enable the gamemode icon next to the tier")));
        }).dimensions(width / 2 + 2, distance, 88, 20).build();
        toggleShowIcons.setTooltip(Tooltip.of(Text.of(ItaTiersClient.showIcons ? "Disable the gamemode icon next to the tier" : "Enable the gamemode icon next to the tier")));

        toggleSeparatorMode = ButtonWidget.builder(Text.literal(ItaTiersClient.isSeparatorAdaptive ? "Disable Dynamic Separator" : "Enable Dynamic Separator").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> {
            ItaTiersClient.toggleSeparatorAdaptive();
            buttonWidget.setMessage(Text.literal(ItaTiersClient.isSeparatorAdaptive ? "Disable Dynamic Separator" : "Enable Dynamic Separator").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
            toggleSeparatorMode.setTooltip(Tooltip.of(Text.of(ItaTiersClient.isSeparatorAdaptive ? "Make the Tiers separator gray" : "Make the Tiers separator match the tier color")));
        }).dimensions(width / 2 - 90, distance + 25, 180, 20).build();
        toggleSeparatorMode.setTooltip(Tooltip.of(Text.of(ItaTiersClient.isSeparatorAdaptive ? "Make the Tiers separator gray" : "Make the Tiers separator match the tier color")));

        cycleDisplayMode = ButtonWidget.builder(Text.literal(ItaTiersClient.displayMode.getCurrentMode()).setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> {
            ItaTiersClient.cycleDisplayMode();
            buttonWidget.setMessage(Text.literal(ItaTiersClient.displayMode.getCurrentMode()).setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
        }).dimensions(width / 2 - 90, distance + 50, 180, 20).build();
        cycleDisplayMode.setTooltip(Tooltip.of(Text.of("""
                Selected: only the selected tier will be displayed
                
                Highest: only the highest tier will be displayed
                
                Adaptive Highest: the highest tier will be displayed if selected does not exist""")));

        positionItaTiers = ButtonWidget.builder(Text.of(ItaTiersClient.positionItaTiers.getStatus()), (buttonWidget) -> {
            ItaTiersClient.cycleItaTiersPosition();
            buttonWidget.setMessage(Text.of(ItaTiersClient.positionItaTiers.getStatus()));
        }).dimensions(width / 2 - 88 - 2, distance + 75, 88, 20).build();

        positionItaTiers.setTooltip(Tooltip.of(Text.of("""
                Right: Tiers will be displayed on the right of the nametag
                
                Left: Tiers will be displayed on the left of the nametag""")));

        activeMode = ButtonWidget.builder(Icons.CYCLE, (buttonWidget) -> ItaTiersClient.cycleModes()).dimensions(width / 2 + 2, distance + 75, 44, 20).build();
        activeMode.setTooltip(Tooltip.of(Text.of("Cycle active gamemode")));

        if (ownProfile.status == Status.READY) {
            enableOwnProfile = ButtonWidget.builder(Text.literal(useOwnProfile ? "Preview default" : "Preview " + ownProfile.name).setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> {
                useOwnProfile = !useOwnProfile;

                loadPlayerAvatar();

                buttonWidget.setMessage(Text.literal(useOwnProfile ? "Preview default" : "Preview " + ownProfile.name).setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
                enableOwnProfile.setTooltip(Tooltip.of(Text.of(useOwnProfile ? "Preview the default profile (" + defaultProfile.name + ")" : "Preview your player profile (" + ownProfile.name + ")")));
            }).dimensions(width / 2 - 90, distance + 100, 180, 20).build();
            enableOwnProfile.setTooltip(Tooltip.of(Text.of(useOwnProfile ? "Preview the default profile (" + defaultProfile.name + ")" : "Preview your player profile (" + ownProfile.name + ")")));
        } else {
            enableOwnProfile = ButtonWidget.builder(Text.literal("Cannot switch profiles").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> {
            }).dimensions(width / 2 - 90, distance + 100, 180, 20).build();
            enableOwnProfile.setTooltip(Tooltip.of(Text.of("Can't switch profiles: " + ownProfile.name + " is not found or fetched yet")));
        }

        clearPlayerCache = ButtonWidget.builder(Text.literal("Clear cache").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> ItaTiersClient.clearCache(false)).dimensions(width - 88 - 10, height - 20 - 10, 88, 20).build();
        clearPlayerCache.setTooltip(Tooltip.of(Text.of("Clear all player cache")));

        this.addDrawableChild(toggleModWidget);
        this.addDrawableChild(toggleShowIcons);
        this.addDrawableChild(toggleSeparatorMode);
        this.addDrawableChild(cycleDisplayMode);
        this.addDrawableChild(positionItaTiers);
        this.addDrawableChild(activeMode);
        this.addDrawableChild(enableOwnProfile);
        this.addDrawableChild(clearPlayerCache);
    }

    private void drawPlayerAvatar(DrawContext context, int x, int y) {
        if (playerAvatarTexture != null && imageReady)
            context.drawTexture(RenderLayer::getGuiTextured, playerAvatarTexture, x - width / 32, y, 0, 0, width / 16, (int) (width / 6.666), width / 16, (int) (width / 6.666));
        else if (ownProfile.numberOfImageRequests > 4)
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(ownProfile.name + "'s skin failed to load. Clear cache and retry"), x, y + 40, ColorControl.getColorMinecraftStandard("red"));
        else
            loadPlayerAvatar();
    }

    private void loadPlayerAvatar() {
        File avatarFile = FabricLoader.getInstance().getGameDir().resolve("cache/itatiers/" + (useOwnProfile ? ownProfile.uuid : defaultProfile.uuid) + ".png").toFile();
        if (!avatarFile.exists())
            return;

        try (FileInputStream stream = new FileInputStream(avatarFile)) {
            playerAvatarTexture = Identifier.of("itatiers", (useOwnProfile ? ownProfile.uuid : defaultProfile.uuid));
            MinecraftClient.getInstance().getTextureManager().registerTexture(playerAvatarTexture, new NativeImageBackedTexture(NativeImage.read(stream)));
            imageReady = true;
        } catch (IOException ignored) {
        }
    }

    public static Screen getConfigScreen(Screen ignoredScreen) {
        return new ConfigScreen();
    }
}