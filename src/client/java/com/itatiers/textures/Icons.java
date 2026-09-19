package com.itatiers.textures;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class Icons {
    private static final Style itaGamemodesStyle = Style.EMPTY.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "ita_gamemodes")));
    private static final Style itaGamemodesTagsStyle = Style.EMPTY.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "ita_gamemodes-tags")));
    private static final Style itaMiscStyle = Style.EMPTY.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "ita_misc")));

    public static final Component VANILLA = Component.literal("\uF010").setStyle(itaGamemodesStyle);
    public static final Component SWORD = Component.literal("\uF011").setStyle(itaGamemodesStyle);
    public static final Component UHC = Component.literal("\uF012").setStyle(itaGamemodesStyle);
    public static final Component POT = Component.literal("\uF013").setStyle(itaGamemodesStyle);
    public static final Component NETH_POT = Component.literal("\uF014").setStyle(itaGamemodesStyle);
    public static final Component SMP = Component.literal("\uF015").setStyle(itaGamemodesStyle);
    public static final Component AXE = Component.literal("\uF016").setStyle(itaGamemodesStyle);
    public static final Component ELYTRA = Component.literal("\uF017").setStyle(itaGamemodesStyle);

    public static Component OVERALL = Component.literal("\uF001").setStyle(Style.EMPTY.withColor(ColorControl.getColorMinecraftStandard("points")).withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "ita_misc"))));
    public static final Component CYCLE = Component.literal("\uF002").setStyle(itaMiscStyle);
    public static final Component NAMEMC = Component.literal("\uF003").setStyle(itaMiscStyle);
    public static final Component IT_FLAG = Component.literal("\uF004").setStyle(itaMiscStyle);
    public static final Component IT_FLAG_BUTTON = Component.literal("\uF005").setStyle(itaMiscStyle);
    public static final Component NO_IT_FLAG_BUTTON = Component.literal("\uF006").setStyle(itaMiscStyle);

    public static final Component VANILLA_TAG = Component.literal("\uF010").setStyle(itaGamemodesTagsStyle);
    public static final Component SWORD_TAG = Component.literal("\uF011").setStyle(itaGamemodesTagsStyle);
    public static final Component UHC_TAG = Component.literal("\uF012").setStyle(itaGamemodesTagsStyle);
    public static final Component POT_TAG = Component.literal("\uF013").setStyle(itaGamemodesTagsStyle);
    public static final Component NETH_POT_TAG = Component.literal("\uF014").setStyle(itaGamemodesTagsStyle);
    public static final Component SMP_TAG = Component.literal("\uF015").setStyle(itaGamemodesTagsStyle);
    public static final Component AXE_TAG = Component.literal("\uF016").setStyle(itaGamemodesTagsStyle);
    public static final Component ELYTRA_TAG = Component.literal("\uF017").setStyle(itaGamemodesTagsStyle);
}