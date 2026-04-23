package com.itatiers.textures;

import com.google.gson.JsonObject;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class ColorControl {
    private static final HashMap<String, Integer> colors = new HashMap<>();

    public static void updateColors(JsonObject json) {
        colors.clear();
        for (String key : json.keySet())
            colors.put(key, Integer.parseUnsignedInt(json.get(key).getAsString().replace("#", ""), 16));

        Icons.OVERALL = Text.literal("\uF001").setStyle(Style.EMPTY.withColor(getColorMinecraftStandard("points")).withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "misc"))));
    }

    public static int getColor(String name) {
        return colors.getOrDefault(name, 0xaaaaaa);
    }

    public static int getColorMinecraftStandard(String name) {
        return 0xff000000 | (colors.getOrDefault(name, 0xaaaaaa) & 0x00ffffff);
    }
}