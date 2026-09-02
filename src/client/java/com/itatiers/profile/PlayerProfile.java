package com.itatiers.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itatiers.ItaTiersClient;
import com.itatiers.profile.types.ItaTiersProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.itatiers.ItaTiersClient.*;

public class PlayerProfile {
    public Status status = Status.SEARCHING;

    public String name = "";
    public String uuid = "";
    public UUID uuidObject;

    public ItaTiersProfile profileItaTiers;

    public Text originalNameText;
    public String originalName;
    public boolean imageSaved = false;
    public int numberOfImageRequests = 0;
    private final boolean regular;

    public PlayerProfile(String name, boolean regular) {
        this.regular = regular;
        this.name = name;
        originalNameText = Text.of(name);
        originalName = name;
    }

    public PlayerProfile(String mojangJson, String jsonItaTiers) {
        regular = false;
        JsonObject jsonObject = JsonParser.parseString(mojangJson).getAsJsonObject();

        if (jsonObject.has("name") && jsonObject.has("id")) {
            this.name = jsonObject.get("name").getAsString();
            uuid = jsonObject.get("id").getAsString();
            originalNameText = Text.of(name);
        } else {
            status = Status.NOT_EXISTING;
            return;
        }

        final Identifier DEFAULT_IMAGE = Identifier.of("minecraft", "textures/ita_default.png");
        Path targetFile = FabricLoader.getInstance().getGameDir().resolve("cache/itatiers/da300ba3690b43228feacf1628825c88.png");

        try (InputStream inputStream = MinecraftClient.getInstance().getResourceManager().getResource(DEFAULT_IMAGE).orElseThrow().getInputStream()) {
            if (inputStream == null) throw new IOException();

            Files.createDirectories(targetFile.getParent());
            Files.copy(inputStream, targetFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
            LOGGER.warn("Error copying default image");
        }

        profileItaTiers = new ItaTiersProfile(jsonItaTiers);

        status = Status.READY;
    }

    public static void buildItaTiersRequests(ArrayList<PlayerProfile> playerProfiles) {
        if (playerProfiles.isEmpty())
            return;

        JsonObject jsonBodyObject = new JsonObject();
        JsonArray namesArray = new JsonArray();

        for (PlayerProfile profile : playerProfiles)
            if (profile.name != null)
                namesArray.add(profile.name);

        jsonBodyObject.add("names", namesArray);
        String jsonBody = jsonBodyObject.toString();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mctiers.it/api/mod/v1/profiles"))
                    .header("User-Agent", "ItaTiers/" + version)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            ItaTiersClient.HTTP_CLIENT
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() != 200) {
                            for (PlayerProfile playerProfile : playerProfiles)
                                playerProfile.status = Status.API_ISSUE;

                            return;
                        }

                        if (!parseItaTiersProfilesJson(response.body(), playerProfiles))
                            for (PlayerProfile playerProfile : playerProfiles)
                                playerProfile.status = Status.API_ISSUE;
                    })
                    .exceptionally(exception -> {
                        for (PlayerProfile playerProfile : playerProfiles)
                            playerProfile.status = Status.API_ISSUE;

                        return null;
                    });
        } catch (IllegalArgumentException ignored) {
            for (PlayerProfile playerProfile : playerProfiles)
                playerProfile.status = Status.API_ISSUE;
        }
    }

    private static boolean parseItaTiersProfilesJson(String json, ArrayList<PlayerProfile> playerProfiles) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        ArrayList<PlayerProfile> notFound = new ArrayList<>(playerProfiles);

        if (jsonObject.has("profiles") && jsonObject.has("version")) {
            JsonArray profiles = jsonObject.getAsJsonArray("profiles");

            for (JsonElement profileElement : profiles) {
                JsonObject profileObj = profileElement.getAsJsonObject();

                if (!profileObj.has("username"))
                    continue;

                String username = profileObj.get("username").getAsString();

                for (PlayerProfile playerProfile : playerProfiles) {
                    if (playerProfile != null && username.equalsIgnoreCase(playerProfile.name)) {
                        playerProfile.parseItaTiersJson(profileObj);
                        notFound.remove(playerProfile);
                        break;
                    }
                }
            }

            for (PlayerProfile playerProfile : notFound)
                playerProfile.status = Status.NOT_EXISTING;
            return true;
        } else {
            return false;
        }
    }

    private void parseItaTiersJson(JsonObject jsonObject) {
        if (jsonObject != null && jsonObject.has("username") && jsonObject.has("uuid")) {
            name = jsonObject.get("username").getAsString();
            uuid = jsonObject.get("uuid").getAsString();
        } else {
            this.status = Status.API_ISSUE;
            return;
        }

        if (!regular)
            savePlayerImage();

        profileItaTiers = new ItaTiersProfile(jsonObject.toString());

        status = Status.READY;
    }

    public void savePlayerImage() {
        if (numberOfImageRequests == 5)
            return;
        numberOfImageRequests++;
        String savePath = FabricLoader.getInstance().getGameDir() + "/cache/itatiers/" + (regular ? "players/" : "");
        CompletableFuture.runAsync(() -> {
            try {
                Files.createDirectories(Paths.get(savePath));
                URL uri = new URI("https://mc-heads.net/body/" + uuid).toURL();
                HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
                connection.setRequestProperty("User-Agent", userAgent);
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                try (InputStream inputStream = connection.getInputStream()) {
                    BufferedImage image = ImageIO.read(inputStream);
                    File outputFile = new File(savePath + uuid + ".png");
                    ImageIO.write(image, "png", outputFile);
                    imageSaved = true;
                }
            } catch (IOException | URISyntaxException ignored) {
                CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS).execute(this::savePlayerImage);
            }
        });
    }

    public void resetDrawnStatus() {
        if (profileItaTiers == null)
            return;
        profileItaTiers.drawn = false;
        for (GameMode mode : profileItaTiers.gameModes)
            mode.drawn = false;
    }
}