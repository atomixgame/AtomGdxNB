package com.atomgdx.core.viewport;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a device resolution profile for Unity-style Game View simulation in AtomGDX.
 */
public class DeviceResolution implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Category {
        FREE_ASPECT,
        DESKTOP_CONSOLE,
        MOBILE_PHONE,
        TABLET,
        CUSTOM
    }

    public enum Orientation {
        LANDSCAPE,
        PORTRAIT
    }

    private final String name;
    private final int width;
    private final int height;
    private final String aspectRatio;
    private final Category category;
    private final int notchTop;
    private final int homeBarBottom;

    public DeviceResolution(String name, int width, int height, String aspectRatio, Category category) {
        this(name, width, height, aspectRatio, category, 0, 0);
    }

    public DeviceResolution(String name, int width, int height, String aspectRatio, Category category, int notchTop, int homeBarBottom) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.aspectRatio = aspectRatio;
        this.category = category;
        this.notchTop = notchTop;
        this.homeBarBottom = homeBarBottom;
    }

    public String getName() { return name; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getAspectRatio() { return aspectRatio; }
    public Category getCategory() { return category; }
    public int getNotchTop() { return notchTop; }
    public int getHomeBarBottom() { return homeBarBottom; }
    public boolean isFreeAspect() { return width <= 0 || height <= 0; }

    public int getEffectiveWidth(Orientation orientation) {
        if (isFreeAspect()) return 0;
        return orientation == Orientation.LANDSCAPE ? Math.max(width, height) : Math.min(width, height);
    }

    public int getEffectiveHeight(Orientation orientation) {
        if (isFreeAspect()) return 0;
        return orientation == Orientation.LANDSCAPE ? Math.min(width, height) : Math.max(width, height);
    }

    /**
     * Computes the letterboxed viewport rectangle within the available container component.
     */
    public Rectangle calculateLetterboxBounds(int containerW, int containerH, Orientation orientation) {
        if (isFreeAspect() || containerW <= 0 || containerH <= 0) {
            return new Rectangle(0, 0, containerW, containerH);
        }

        int targetW = getEffectiveWidth(orientation);
        int targetH = getEffectiveHeight(orientation);

        float targetAspect = (float) targetW / (float) targetH;
        float containerAspect = (float) containerW / (float) containerH;

        int finalW, finalH;
        if (containerAspect > targetAspect) {
            // Letterbox left and right
            finalH = containerH;
            finalW = Math.round(containerH * targetAspect);
        } else {
            // Letterbox top and bottom
            finalW = containerW;
            finalH = Math.round(containerW / targetAspect);
        }

        int finalX = (containerW - finalW) / 2;
        int finalY = (containerH - finalH) / 2;

        return new Rectangle(finalX, finalY, finalW, finalH);
    }

    @Override
    public String toString() {
        if (isFreeAspect()) return name;
        return name + " (" + width + "x" + height + " " + aspectRatio + ")";
    }

    public static List<DeviceResolution> getStandardPresets() {
        List<DeviceResolution> list = new ArrayList<>();
        // Free Aspect
        list.add(new DeviceResolution("Free Aspect", 0, 0, "Fluid", Category.FREE_ASPECT));

        // Desktop & Console
        list.add(new DeviceResolution("1080p Full HD", 1920, 1080, "16:9", Category.DESKTOP_CONSOLE));
        list.add(new DeviceResolution("720p HD / Switch", 1280, 720, "16:9", Category.DESKTOP_CONSOLE));
        list.add(new DeviceResolution("1440p QHD 2K", 2560, 1440, "16:9", Category.DESKTOP_CONSOLE));
        list.add(new DeviceResolution("4K Ultra HD", 3840, 2160, "16:9", Category.DESKTOP_CONSOLE));
        list.add(new DeviceResolution("Steam Deck", 1280, 800, "16:10", Category.DESKTOP_CONSOLE));
        list.add(new DeviceResolution("Ultrawide 21:9", 2560, 1080, "21:9", Category.DESKTOP_CONSOLE));

        // Mobile Phones
        list.add(new DeviceResolution("iPhone 15 / Pro", 1179, 2556, "19.5:9", Category.MOBILE_PHONE, 59, 34));
        list.add(new DeviceResolution("iPhone SE (3rd gen)", 750, 1334, "16:9", Category.MOBILE_PHONE, 0, 0));
        list.add(new DeviceResolution("Google Pixel 8", 1080, 2400, "20:9", Category.MOBILE_PHONE, 40, 24));
        list.add(new DeviceResolution("Samsung Galaxy S24", 1080, 2340, "19.5:9", Category.MOBILE_PHONE, 42, 24));

        // Tablets
        list.add(new DeviceResolution("iPad Pro 12.9\"", 2048, 2732, "4:3", Category.TABLET, 0, 20));
        list.add(new DeviceResolution("iPad Air 10.9\"", 1640, 2360, "3:2", Category.TABLET, 0, 20));
        list.add(new DeviceResolution("Galaxy Tab S9", 1600, 2560, "16:10", Category.TABLET, 0, 20));

        return Collections.unmodifiableList(list);
    }
}
