package com.atomgdx.editor.scene2d.data.vo;

/**
 * Text Label item in HyperLap2D.
 */
public class LabelVO extends MainItemVO {
    private static final long serialVersionUID = 1L;

    public String text = "New Label";
    public String style = "default";
    public int size = 16;
    public int align = 8; // Align.center
    public float width = 120f;
    public float height = 30f;
    public boolean wrap = false;

    public LabelVO() {}

    public LabelVO(String text, float x, float y) {
        this.text = text;
        this.itemName = text;
        this.x = x;
        this.y = y;
    }
}
