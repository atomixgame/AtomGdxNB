package com.neon.cosmos.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.neon.cosmos.NeonCosmosGame;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Neon Cosmos (AtomGdx Studio Demo)");
        config.setWindowedMode(1280, 720);
        config.useVsync(true);
        new Lwjgl3Application(new NeonCosmosGame(), config);
    }
}
