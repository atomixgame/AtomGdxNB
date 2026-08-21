package com.atomgdx.genart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;

/**
 * Service for generative art, placeholder textures, and prompt-to-sprite asset generation.
 */
public class GenerativeArtService {

    public enum TexturePreset {
        CHECKERBOARD,
        RADIAL_GRADIENT,
        NOISE,
        NEON_GRID
    }

    public static CompletableFuture<File> generateProceduralTexture(
            File outputFile,
            int width,
            int height,
            TexturePreset preset,
            Color primaryColor,
            Color secondaryColor
    ) {
        return CompletableFuture.supplyAsync(() -> {
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();

            if (preset == TexturePreset.CHECKERBOARD) {
                int tileSize = Math.max(8, width / 8);
                for (int y = 0; y < height; y += tileSize) {
                    for (int x = 0; x < width; x += tileSize) {
                        boolean isEven = ((x / tileSize) + (y / tileSize)) % 2 == 0;
                        g.setColor(isEven ? primaryColor : secondaryColor);
                        g.fillRect(x, y, tileSize, tileSize);
                    }
                }
            } else if (preset == TexturePreset.NEON_GRID) {
                g.setColor(secondaryColor);
                g.fillRect(0, 0, width, height);
                g.setColor(primaryColor);
                int step = Math.max(16, width / 16);
                for (int x = 0; x < width; x += step) {
                    g.drawLine(x, 0, x, height);
                }
                for (int y = 0; y < height; y += step) {
                    g.drawLine(0, y, width, y);
                }
            } else if (preset == TexturePreset.RADIAL_GRADIENT) {
                float cx = width / 2.0f;
                float cy = height / 2.0f;
                float maxR = (float) Math.hypot(cx, cy);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        float dist = (float) Math.hypot(x - cx, y - cy);
                        float t = Math.min(1.0f, Math.max(0.0f, dist / maxR));
                        int r = (int) (primaryColor.getRed() * (1f - t) + secondaryColor.getRed() * t);
                        int gr = (int) (primaryColor.getGreen() * (1f - t) + secondaryColor.getGreen() * t);
                        int b = (int) (primaryColor.getBlue() * (1f - t) + secondaryColor.getBlue() * t);
                        int a = (int) (primaryColor.getAlpha() * (1f - t) + secondaryColor.getAlpha() * t);
                        img.setRGB(x, y, (a << 24) | (r << 16) | (gr << 8) | b);
                    }
                }
            } else if (preset == TexturePreset.NOISE) {
                java.util.Random rnd = new java.util.Random(1337);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        float n = (float) (Math.sin(x * 0.1) * Math.cos(y * 0.1) * 0.5 + 0.5);
                        float randFactor = rnd.nextFloat() * 0.25f;
                        float t = Math.min(1.0f, Math.max(0.0f, n * 0.75f + randFactor));
                        int r = (int) (primaryColor.getRed() * (1f - t) + secondaryColor.getRed() * t);
                        int gr = (int) (primaryColor.getGreen() * (1f - t) + secondaryColor.getGreen() * t);
                        int b = (int) (primaryColor.getBlue() * (1f - t) + secondaryColor.getBlue() * t);
                        int a = (int) (primaryColor.getAlpha() * (1f - t) + secondaryColor.getAlpha() * t);
                        img.setRGB(x, y, (a << 24) | (r << 16) | (gr << 8) | b);
                    }
                }
            } else {
                g.setColor(primaryColor);
                g.fillRect(0, 0, width, height);
            }

            g.dispose();

            try {
                if (outputFile.getParentFile() != null) {
                    outputFile.getParentFile().mkdirs();
                }
                ImageIO.write(img, "png", outputFile);
                return outputFile;
            } catch (IOException e) {
                throw new RuntimeException("Failed to save texture: " + e.getMessage(), e);
            }
        });
    }
}
