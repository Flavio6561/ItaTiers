package com.itatiers.profile.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itatiers.misc.Modes;
import com.itatiers.profile.GameMode;
import com.itatiers.textures.ColorControl;
import com.itatiers.profile.Status;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.itatiers.ItaTiersClient.userAgent;

public class SuperProfile {
    public Status status = Status.SEARCHING;

    public int points;

    public Text displayedPoints;
    public Text pointsTooltip;

    public final ArrayList<GameMode> gameModes = new ArrayList<>();

    public GameMode highest;
    public String originalJson;
    public boolean drawn = false;
    private int numberOfRequests = 0;

    protected SuperProfile(String name, String apiUrl) {
        CompletableFuture.delayedExecutor(20, TimeUnit.MILLISECONDS).execute(() -> buildRequest(name, apiUrl));
    }

    protected SuperProfile(String json) {
        CompletableFuture.delayedExecutor(20, TimeUnit.MILLISECONDS).execute(() -> parseJson(json));
    }

    private void buildRequest(String name, String apiUrl) {
        if (numberOfRequests == 5 || status != Status.SEARCHING) {
            status = Status.TIMEOUTED;
            return;
        }

        numberOfRequests++;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + name))
                .header("User-Agent", userAgent)
                .GET()
                .build();

        HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 404) {
                        status = Status.NOT_EXISTING;
                        return;
                    } else if (response.statusCode() != 200) {
                        status = Status.API_ISSUE;
                        return;
                    }

                    parseJson(response.body());
                })
                .exceptionally(exception -> {
                    CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(() -> buildRequest(name, apiUrl));
                    return null;
                });
    }

    public void parseJson(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if (jsonObject.has("username") && jsonObject.has("uuid") &&
                jsonObject.has("tiers") && jsonObject.has("points")) {
            points = jsonObject.get("points").getAsInt();
        } else {
            status = Status.NOT_EXISTING;
            return;
        }

        displayedPoints = getPointsText();
        pointsTooltip = getPointsTooltip();

        parseRankings(jsonObject.getAsJsonObject("tiers"));

        status = Status.READY;
        originalJson = json;
    }

    private void parseRankings(JsonObject jsonObject) {
        for (GameMode gameMode : gameModes) {
            if (jsonObject.has(gameMode.parsingName))
                gameMode.parseTiers(jsonObject.get(gameMode.parsingName));
            else
                gameMode.status = Status.NOT_EXISTING;
        }
        highest = getHighestMode();
    }

    public GameMode getGameMode(Modes gamemode) {
        for (GameMode gameMode : gameModes)
            if (gameMode.name.toString().equalsIgnoreCase(gamemode.toString()))
                return gameMode;

        status = Status.NOT_EXISTING;
        return null;
    }

    private GameMode getHighestMode() {
        GameMode highest = null;
        int highestPoints = 0;
        for (GameMode gameMode : gameModes) {
            if (gameMode.status == Status.READY && gameMode.getTierPoints() > highestPoints) {
                highest = gameMode;
                highestPoints = gameMode.getTierPoints();
            }
        }
        return highest;
    }

    private Text getPointsText() {
        if (points >= 200) return Text.literal(String.valueOf(points)).setStyle(Style.EMPTY.withColor(ColorControl.getColor("master")));
        else if (points >= 100) return Text.literal(String.valueOf(points)).setStyle(Style.EMPTY.withColor(ColorControl.getColor("ace")));
        else if (points >= 50) return Text.literal(String.valueOf(points)).setStyle(Style.EMPTY.withColor(ColorControl.getColor("specialist")));
        else if (points >= 20) return Text.literal(String.valueOf(points)).setStyle(Style.EMPTY.withColor(ColorControl.getColor("cadet")));
        else if (points >= 10) return Text.literal(String.valueOf(points)).setStyle(Style.EMPTY.withColor(ColorControl.getColor("novice")));

        return Text.literal(String.valueOf(points)).setStyle(Style.EMPTY.withColor(ColorControl.getColor("rookie")));
    }

    private Text getPointsTooltip() {
        String overallTooltip = "Combat ";

        if (points >= 200) overallTooltip += "Master";
        else if (points >= 100) overallTooltip += "Ace";
        else if (points >= 50) overallTooltip += "Specialist";
        else if (points >= 20) overallTooltip += "Cadet";
        else if (points >= 10) overallTooltip += "Novice";
        else overallTooltip = "Rookie";

        return Text.literal(overallTooltip).setStyle(displayedPoints.getStyle());
    }
}