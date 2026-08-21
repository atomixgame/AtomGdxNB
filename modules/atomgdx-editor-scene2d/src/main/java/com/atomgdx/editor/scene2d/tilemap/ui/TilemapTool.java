package com.atomgdx.editor.scene2d.tilemap.ui;

public enum TilemapTool {
    PAINT_BRUSH("Paint / Stamp", "Paint tiles directly or stamp multi-cell blocks"),
    BUCKET_FILL("Bucket Flood Fill", "4-Way flood fill connected regions"),
    ERASER("Eraser", "Clear tiles from active layer"),
    RECT_FILL("Rectangle Fill", "Drag bounding box to fill rectangular tile region"),
    LINE("Line Tool", "Draw straight lines of tiles"),
    EYEDROPPER("Eyedropper", "Pick tile under cursor into active brush"),
    MARQUEE_SELECT("Marquee Select", "Select rectangular tile block (Cut/Copy/Paste)"),
    RANDOM_BRUSH("Random Weighted", "Place randomly chosen tile from selected set"),
    AUTOTILE_RULE("Rule Tile (Auto-Tile)", "Automatic 3x3 neighbor edge/corner resolution");

    private final String displayName;
    private final String tooltip;

    TilemapTool(String displayName, String tooltip) {
        this.displayName = displayName;
        this.tooltip = tooltip;
    }

    public String getDisplayName() { return displayName; }
    public String getTooltip() { return tooltip; }

    @Override
    public String toString() { return displayName; }
}
