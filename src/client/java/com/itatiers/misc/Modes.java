package com.itatiers.misc;

import com.itatiers.textures.ColorControl;
import com.itatiers.textures.Icons;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;

public enum Modes {
    VANILLA(Icons.VANILLA, Icons.VANILLA_TAG, "vanilla", "Crystal"),
    UHC(Icons.UHC, Icons.UHC_TAG, "uhc", "UHC"),
    SWORD(Icons.SWORD, Icons.SWORD_TAG, "sword", "Sword"),
    SMP(Icons.SMP, Icons.SMP_TAG, "smp", "Smp"),
    NETH_POT(Icons.NETH_POT, Icons.NETH_POT_TAG, "nethpot", "Netherite Pot"),
    ELYTRA(Icons.ELYTRA, Icons.ELYTRA_TAG, "elytra", "Elytra"),
    POT(Icons.POT, Icons.POT_TAG, "diapot", "Diamond Pot"),
    AXE(Icons.AXE, Icons.AXE_TAG, "axe", "Axe");

    public final Text icon;
    public final Text iconTag;
    private final String color;
    private final String stringLabel;
    public Text label;

    Modes(Text icon, Text iconTag, String color, String label) {
        this.icon = icon;
        this.iconTag = iconTag;
        this.color = color;
        this.stringLabel = label;
        this.label = Text.literal(label).setStyle(Style.EMPTY.withColor(ColorControl.getColor(color)));
    }

    public static void updateColors() {
        for (Modes mode : values())
            mode.label = Text.literal(mode.stringLabel).setStyle(Style.EMPTY.withColor(ColorControl.getColor(mode.color)));
    }

    public static Modes[] getValues() {
        Modes[] modesArray = new Modes[7];
        ArrayList<Modes> modes = new ArrayList<>(Arrays.asList(Modes.values()));
        return modes.toArray(modesArray);
    }
}