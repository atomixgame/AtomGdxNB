package com.atomgdx.tools.texturepacker;

import java.io.Serializable;

/**
 * Configuration options for Texture Atlas packing.
 */
public class TexturePackerSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maxWidth = 2048;
    private int maxHeight = 2048;
    private int minWidth = 16;
    private int minHeight = 16;
    private int paddingX = 2;
    private int paddingY = 2;
    private boolean edgePadding = true;
    private boolean duplicatePadding = false;
    private boolean rotation = false;
    private boolean stripWhitespaceX = true;
    private boolean stripWhitespaceY = true;
    private boolean square = false;
    private String format = "RGBA8888";
    private String filterMin = "Linear";
    private String filterMag = "Linear";
    private String atlasExtension = ".atlas";

    public TexturePackerSettings() {
    }

    public int getMaxWidth() { return maxWidth; }
    public void setMaxWidth(int maxWidth) { this.maxWidth = maxWidth; }

    public int getMaxHeight() { return maxHeight; }
    public void setMaxHeight(int maxHeight) { this.maxHeight = maxHeight; }

    public int getMinWidth() { return minWidth; }
    public void setMinWidth(int minWidth) { this.minWidth = minWidth; }

    public int getMinHeight() { return minHeight; }
    public void setMinHeight(int minHeight) { this.minHeight = minHeight; }

    public int getPaddingX() { return paddingX; }
    public void setPaddingX(int paddingX) { this.paddingX = paddingX; }

    public int getPaddingY() { return paddingY; }
    public void setPaddingY(int paddingY) { this.paddingY = paddingY; }

    public boolean isEdgePadding() { return edgePadding; }
    public void setEdgePadding(boolean edgePadding) { this.edgePadding = edgePadding; }

    public boolean isDuplicatePadding() { return duplicatePadding; }
    public void setDuplicatePadding(boolean duplicatePadding) { this.duplicatePadding = duplicatePadding; }

    public boolean isRotation() { return rotation; }
    public void setRotation(boolean rotation) { this.rotation = rotation; }

    public boolean isStripWhitespaceX() { return stripWhitespaceX; }
    public void setStripWhitespaceX(boolean stripWhitespaceX) { this.stripWhitespaceX = stripWhitespaceX; }

    public boolean isStripWhitespaceY() { return stripWhitespaceY; }
    public void setStripWhitespaceY(boolean stripWhitespaceY) { this.stripWhitespaceY = stripWhitespaceY; }

    public boolean isSquare() { return square; }
    public void setSquare(boolean square) { this.square = square; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getFilterMin() { return filterMin; }
    public void setFilterMin(String filterMin) { this.filterMin = filterMin; }

    public String getFilterMag() { return filterMag; }
    public void setFilterMag(String filterMag) { this.filterMag = filterMag; }

    public String getAtlasExtension() { return atlasExtension; }
    public void setAtlasExtension(String atlasExtension) { this.atlasExtension = atlasExtension; }
}
