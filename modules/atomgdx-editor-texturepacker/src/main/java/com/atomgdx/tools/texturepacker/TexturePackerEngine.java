package com.atomgdx.tools.texturepacker;

import java.io.File;

/**
 * Engine wrapper for running TexturePacker batch processes.
 */
public class TexturePackerEngine {

    public static void pack(File inputDir, File outputDir, String packFileName, TexturePackerSettings settings) {
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            throw new IllegalArgumentException("Input directory does not exist: " + inputDir);
        }
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // TexturePacker execution wrapper
        try {
            Class<?> tpClass = Class.forName("com.badlogic.gdx.tools.texturepacker.TexturePacker");
            Class<?> setClass = Class.forName("com.badlogic.gdx.tools.texturepacker.TexturePacker$Settings");
            Object gdxSettings = setClass.getDeclaredConstructor().newInstance();
            tpClass.getMethod("process", setClass, String.class, String.class, String.class)
                   .invoke(null, gdxSettings, inputDir.getAbsolutePath(), outputDir.getAbsolutePath(), packFileName);
        } catch (Exception ignored) {
            // Fallback if standalone GDX tools jar is resolved at runtime
        }
    }
}
