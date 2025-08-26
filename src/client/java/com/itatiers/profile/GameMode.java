package com.itatiers.profile;

import com.google.gson.JsonElement;
import com.itatiers.misc.Modes;
import com.itatiers.textures.ColorControl;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class GameMode {
    public Status status = Status.SEARCHING;

    private String tier;

    public Text displayedTier;
    private String displayedTierUnformatted;
    public Text tierTooltip;

    public final Modes name;
    public final String parsingName;
    public boolean drawn = false;

    public GameMode(Modes name, String parsingName) {
        this.name = name;
        this.parsingName = parsingName;
    }

    public void parseTiers(JsonElement jsonElement) {
        displayedTierUnformatted = jsonElement.getAsString();
        tier = String.valueOf(displayedTierUnformatted.toCharArray()[2]);

        displayedTier = Text.literal(displayedTierUnformatted).setStyle(Style.EMPTY.withColor(getTierColor(displayedTierUnformatted)));
        tierTooltip = getTierTooltip();

        status = Status.READY;
    }

    private Text getTierTooltip() {
        String tierTooltipString = "";
        if (displayedTierUnformatted.contains("R"))
            tierTooltipString += "Retired ";

        if (displayedTierUnformatted.contains("H"))
            tierTooltipString += "High ";
        else tierTooltipString += "Low ";

        tierTooltipString += "Tier " + tier + "\n\nPoints: " + getTierPoints();

        return Text.literal(tierTooltipString).setStyle(Style.EMPTY.withColor(getTierColor(displayedTierUnformatted)));
    }

    public int getTierPoints() {
        if (status == Status.NOT_EXISTING) return 0;
        String tier = displayedTierUnformatted;
        tier = tier.replace("R", "");

        if (tier.equalsIgnoreCase("HT1")) return 60;
        else if (tier.equalsIgnoreCase("LT1")) return 44;
        else if (tier.equalsIgnoreCase("HT2")) return 28;
        else if (tier.equalsIgnoreCase("LT2")) return 16;
        else if (tier.equalsIgnoreCase("HT3")) return 10;
        else if (tier.equalsIgnoreCase("LT3")) return 6;
        else if (tier.equalsIgnoreCase("HT4")) return 4;
        else if (tier.equalsIgnoreCase("LT4")) return 3;
        else if (tier.equalsIgnoreCase("HT5")) return 2;
        else if (tier.equalsIgnoreCase("LT5")) return 1;
        return 0;
    }

    private int getTierColor(String tier) {
        if (tier.contains("R")) return ColorControl.getColor("retired");
        else if (tier.equalsIgnoreCase("HT1")) return ColorControl.getColor("ht1");
        else if (tier.equalsIgnoreCase("LT1")) return ColorControl.getColor("lt1");
        else if (tier.equalsIgnoreCase("HT2")) return ColorControl.getColor("ht2");
        else if (tier.equalsIgnoreCase("LT2")) return ColorControl.getColor("lt2");
        else if (tier.equalsIgnoreCase("HT3")) return ColorControl.getColor("ht3");
        else if (tier.equalsIgnoreCase("LT3")) return ColorControl.getColor("lt3");
        else if (tier.equalsIgnoreCase("HT4")) return ColorControl.getColor("ht4");
        else if (tier.equalsIgnoreCase("LT4")) return ColorControl.getColor("lt4");
        else if (tier.equalsIgnoreCase("HT5")) return ColorControl.getColor("ht5");
        else if (tier.equalsIgnoreCase("LT5")) return ColorControl.getColor("lt5");
        return ColorControl.getColor("unknown");
    }
}