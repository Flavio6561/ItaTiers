package com.itatiers.profile.types;

import com.itatiers.misc.Modes;
import com.itatiers.profile.GameMode;

public class ItaTiersProfile extends SuperProfile {
    public ItaTiersProfile(String name, String apiUrl) {
        super(name, apiUrl);
        addGamemodes();
    }

    public ItaTiersProfile(String json) {
        super(json);
        addGamemodes();
    }

    public void addGamemodes() {
        gameModes.add(new GameMode(Modes.VANILLA, "vanilla"));
        gameModes.add(new GameMode(Modes.UHC, "uhc"));
        gameModes.add(new GameMode(Modes.SWORD, "sword"));
        gameModes.add(new GameMode(Modes.SMP, "smp"));
        gameModes.add(new GameMode(Modes.NETH_POT, "nethpot"));
        gameModes.add(new GameMode(Modes.ELYTRA, "elytra"));
        gameModes.add(new GameMode(Modes.POT, "diapot"));
        gameModes.add(new GameMode(Modes.AXE, "axe"));
    }
}