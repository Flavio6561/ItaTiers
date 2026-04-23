package com.itatiers.textures;

import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Icons {
    public static final Text VANILLA = Text.literal("\uF010").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes"))));
    public static final Text SWORD = Text.literal("\uF011").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes"))));
    public static final Text UHC = Text.literal("\uF012").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes"))));
    public static final Text POT = Text.literal("\uF013").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes"))));
    public static final Text NETH_POT = Text.literal("\uF014").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes"))));
    public static final Text SMP = Text.literal("\uF015").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes"))));
    public static final Text AXE = Text.literal("\uF016").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes"))));
    public static final Text ELYTRA = Text.literal("\uF017").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes"))));

    public static Text OVERALL = Text.literal("\uF001").setStyle(Style.EMPTY.withColor(ColorControl.getColorMinecraftStandard("points")).withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "misc"))));
    public static final Text CYCLE = Text.literal("\uF002").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "misc"))));
    public static final Text NAMEMC = Text.literal("\uF003").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "misc"))));
    public static final Text IT_FLAG = Text.literal("\uF004").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "misc"))));
    public static final Text IT_FLAG_BUTTON = Text.literal("\uF005").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "misc"))));
    public static final Text NO_IT_FLAG_BUTTON = Text.literal("\uF006").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "misc"))));

    public static final Text VANILLA_TAG = Text.literal("\uF010").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes-tags"))));
    public static final Text SWORD_TAG = Text.literal("\uF011").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes-tags"))));
    public static final Text UHC_TAG = Text.literal("\uF012").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes-tags"))));
    public static final Text POT_TAG = Text.literal("\uF013").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes-tags"))));
    public static final Text NETH_POT_TAG = Text.literal("\uF014").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes-tags"))));
    public static final Text SMP_TAG = Text.literal("\uF015").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes-tags"))));
    public static final Text AXE_TAG = Text.literal("\uF016").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes-tags"))));
    public static final Text ELYTRA_TAG = Text.literal("\uF017").styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "gamemodes-tags"))));
}