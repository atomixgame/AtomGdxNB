package com.atomgdx.editor.scene2d.ui;

import java.io.Serializable;

/**
 * Grid orientation modes for 2D Tilemap and Level design (Orthogonal, Isometric, Hexagonal).
 */
public enum TilemapGridMode implements Serializable {
    ORTHOGONAL("Orthogonal 2D (Standard)", 32, 32),
    ISOMETRIC_DIAMOND("Isometric Diamond (2:1)", 64, 32),
    ISOMETRIC_STAGGERED("Isometric Staggered", 64, 32),
    HEXAGONAL_POINTY("Hexagonal Pointy-Top", 48, 56),
    HEXAGONAL_FLAT("Hexagonal Flat-Top", 56, 48);

    private final String displayName;
    private final int defaultTileWidth;
    private final int defaultTileHeight;

    TilemapGridMode(String displayName, int defaultTileWidth, int defaultTileHeight) {
        this.displayName = displayName;
        this.defaultTileWidth = defaultTileWidth;
        this.defaultTileHeight = defaultTileHeight;
    }

    public String getDisplayName() { return displayName; }
    public int getDefaultTileWidth() { return defaultTileWidth; }
    public int getDefaultTileHeight() { return defaultTileHeight; }

    @Override
    public String toString() { return displayName; }
}
