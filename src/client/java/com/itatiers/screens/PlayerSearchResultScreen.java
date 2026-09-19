package com.itatiers.screens;

import com.itatiers.ItaTiersClient;
import com.itatiers.profile.GameMode;
import com.itatiers.profile.PlayerProfile;
import com.itatiers.profile.Status;
import com.itatiers.profile.types.SuperProfile;
import com.itatiers.textures.ColorControl;
import com.itatiers.textures.Icons;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
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
import java.net.URI;

import static com.itatiers.ItaTiersClient.sendMessageToPlayer;

public class PlayerSearchResultScreen extends Screen {
    private final PlayerProfile playerProfile;
    private Identifier playerAvatarTexture;

    private int separator;
    private boolean imageReady = false;

    public PlayerSearchResultScreen(PlayerProfile playerProfile) {
        super(Component.literal(playerProfile.name));
        this.playerProfile = playerProfile;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (playerProfile.status == Status.NOT_EXISTING) {
            onClose();
            sendMessageToPlayer(playerProfile.name + " was not found in the tierlist", ColorControl.getColor("red"), false);
            return;
        } else if (playerProfile.status == Status.TIMEOUTED) {
            onClose();
            sendMessageToPlayer(playerProfile.name + "'s search was timeouted. Clear cache and retry", ColorControl.getColor("red"), false);
            return;
        } else if (playerProfile.status == Status.API_ISSUE) {
            onClose();
            sendMessageToPlayer(playerProfile.name + "'s search failed. mctiers.it might be down, try again later", ColorControl.getColor("red"), false);
            return;
        }

        int centerX = width / 2;
        int listY = (int) (height / 3.2);
        separator = height / 23;
        int avatarY = height / 55 + 14;

        super.extractRenderState(context, mouseX, mouseY, delta);

        if (playerProfile.status == Status.SEARCHING) {
            context.centeredText(font, Component.literal("Searching for " + playerProfile.name + "..."), centerX, listY, ColorControl.getColorMinecraftStandard("green"));
            return;
        }

        if (playerProfile.numberOfImageRequests == 0)
            playerProfile.savePlayerImage();

        drawPlayerAvatar(context, centerX, avatarY);
        context.centeredText(font, ItaTiersClient.getNametag(playerProfile), centerX, height / 55, ColorControl.getColorMinecraftStandard("text"));

        drawCategoryList(context, playerProfile.profileItaTiers, centerX, listY);
    }

    private void drawCategoryList(GuiGraphicsExtractor context, SuperProfile profile, int x, int y) {
        if (profile == null) {
            context.centeredText(font, "Loading from API...", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("green"));
            return;
        }

        if (profile.status == Status.SEARCHING) {
            context.centeredText(font, "Searching...", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("green"));
            return;
        } else if (profile.status == Status.NOT_EXISTING) {
            context.centeredText(font, "Unranked", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("red"));
            return;
        } else if (profile.status == Status.TIMEOUTED) {
            context.centeredText(font, "Search timeouted. Clear cache and retry", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("red"));
            return;
        } else if (profile.status == Status.API_ISSUE) {
            context.centeredText(font, "Search failed. This is likely an API issue", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("red"));
            context.centeredText(font, "Contact flavio6561 on Discord for support", x, (int) (y + 2.8 * separator + 15), ColorControl.getColorMinecraftStandard("red"));
            return;
        }

        if (!profile.drawn) {
            StringWidget overallLabel = new StringWidget(Component.literal("Points").setStyle(Style.EMPTY.withColor(ColorControl.getColor("points"))), font);
            overallLabel.setPosition(x - 42, (int) (y + 2.4 * separator));
            this.addRenderableWidget(overallLabel);

            StringWidget overallIcon = new StringWidget(Icons.OVERALL, font);
            overallIcon.setPosition(x - 62, (int) (y + 2.4 * separator + 2));
            this.addRenderableWidget(overallIcon);

            StringWidget overall = new StringWidget(profile.displayedPoints, font);
            overall.setPosition(x + 45 - (profile.displayedPoints.getString().length() - 2) * 3, (int) (y + 2.4 * separator));
            overall.setTooltip(Tooltip.create(profile.pointsTooltip));
            this.addRenderableWidget(overall);

            drawTierList(profile, x - 62, (int) (y + 2.4 * separator) + 30);

            profile.drawn = true;
        }
    }

    private void drawTierList(SuperProfile profile, int x, int y) {
        for (GameMode gameMode : profile.gameModes)
            if (drawGameModeTiers(gameMode, x, y)) y += 15;
    }

    private boolean drawGameModeTiers(GameMode mode, int x, int y) {
        if (mode.drawn || mode.status != Status.READY)
            return false;

        StringWidget icon = new StringWidget(mode.name.icon, font);
        icon.setPosition(x, y + 3);
        addRenderableWidget(icon);

        StringWidget label = new StringWidget(mode.name.label, font);
        label.setPosition(x + 20, y);
        addRenderableWidget(label);

        StringWidget tier = new StringWidget(mode.displayedTier, font);
        tier.setPosition(x + 105 - (mode.displayedTier.getString().length() - 3) * 3, y);
        tier.setTooltip(Tooltip.create(mode.tierTooltip));
        addRenderableWidget(tier);

        mode.drawn = true;

        return true;
    }

    private void drawPlayerAvatar(GuiGraphicsExtractor context, int x, int y) {
        if (playerAvatarTexture != null && imageReady)
            context.blit(RenderPipelines.GUI_TEXTURED, playerAvatarTexture, x - width / 32, y, 0, 0, width / 16, (int) (width / 6.666), width / 16, (int) (width / 6.666));
        else if (playerProfile.imageSaved)
            loadPlayerAvatar();
        else if (playerProfile.numberOfImageRequests == 5)
            context.centeredText(font, Component.literal(playerProfile.name + "'s skin failed to load. Clear cache and retry"), x, y + 50, ColorControl.getColorMinecraftStandard("red"));
        else
            context.centeredText(font, Component.literal("Loading " + playerProfile.name + "'s skin"), x, y + 50, ColorControl.getColorMinecraftStandard("green"));
    }

    private void loadPlayerAvatar() {
        File avatarFile = FabricLoader.getInstance().getGameDir().resolve("cache/itatiers/players/" + playerProfile.uuid + ".png").toFile();
        if (!avatarFile.exists())
            return;

        try (FileInputStream stream = new FileInputStream(avatarFile)) {
            playerAvatarTexture = Identifier.fromNamespaceAndPath("players", playerProfile.uuid);
            Minecraft.getInstance().getTextureManager().register(playerAvatarTexture, new DynamicTexture(String::new, NativeImage.read(stream)));
            imageReady = true;
        } catch (IOException ignored) {
        }
    }

    @Override
    protected void init() {
        playerProfile.resetDrawnStatus();

        addRenderableWidget(Button.builder(Icons.NAMEMC, (_) -> ConfirmLinkScreen.confirmLinkNow(Minecraft.getInstance().gui.screen(), URI.create("https://namemc.com/profile/" + playerProfile.uuid), true)).bounds(width - 20 - 5, height - 20 - 5, 20, 20).tooltip(Tooltip.create(Component.literal("Open " + playerProfile.name + "'s NameMC page"))).build());
    }
}