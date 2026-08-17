package com.atomgdx.editor.scene2d.data.vo;

/**
 * Value Object representing a simple sprite image in HyperLap2D.
 */
public class SimpleImageVO extends MainItemVO {
    private static final long serialVersionUID = 1L;

    public String imageName = "";
    public float width = 0f;
    public float height = 0f;

    public SimpleImageVO() {}

    public SimpleImageVO(String imageName, float x, float y) {
        this.imageName = imageName;
        this.itemName = imageName;
        this.x = x;
        this.y = y;
    }
}
