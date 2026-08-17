package com.atomgdx.editor.font;

import java.io.Serializable;

/**
 * Settings for Hiero & FreeType Distance Field (SDF / MSDF) Bitmap Font generation.
 */
public class FontGeneratorSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum FontType {
        BITMAP_STANDARD,
        SDF,  // Signed Distance Field
        MSDF  // Multi-channel Signed Distance Field
    }

    private String fontFilePath = "";
    private int fontSize = 32;
    private FontType fontType = FontType.MSDF;
    private int spread = 4; // SDF spread
    private int padding = 2;
    private int pageWidth = 512;
    private int pageHeight = 512;
    private String characterSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+-=[]{};':\",./<>? ";
    private boolean includeShadow = false;
    private int shadowOffsetX = 2;
    private int shadowOffsetY = 2;

    public FontGeneratorSettings() {
    }

    public String getFontFilePath() { return fontFilePath; }
    public void setFontFilePath(String fontFilePath) { this.fontFilePath = fontFilePath; }

    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }

    public FontType getFontType() { return fontType; }
    public void setFontType(FontType fontType) { this.fontType = fontType; }

    public int getSpread() { return spread; }
    public void setSpread(int spread) { this.spread = spread; }

    public int getPadding() { return padding; }
    public void setPadding(int padding) { this.padding = padding; }

    public int getPageWidth() { return pageWidth; }
    public void setPageWidth(int pageWidth) { this.pageWidth = pageWidth; }

    public int getPageHeight() { return pageHeight; }
    public void setPageHeight(int pageHeight) { this.pageHeight = pageHeight; }

    public String getCharacterSet() { return characterSet; }
    public void setCharacterSet(String characterSet) { this.characterSet = characterSet; }

    public boolean isIncludeShadow() { return includeShadow; }
    public void setIncludeShadow(boolean includeShadow) { this.includeShadow = includeShadow; }

    public int getShadowOffsetX() { return shadowOffsetX; }
    public void setShadowOffsetX(int shadowOffsetX) { this.shadowOffsetX = shadowOffsetX; }

    public int getShadowOffsetY() { return shadowOffsetY; }
    public void setShadowOffsetY(int shadowOffsetY) { this.shadowOffsetY = shadowOffsetY; }
}
