package com.atomgdx.editor.scene2d.data.vo;

/**
 * 9-Patch scalable image component in HyperLap2D.
 */
public class NinePatchVO extends MainItemVO {
    private static final long serialVersionUID = 1L;

    public String imageName = "";
    public float width = 100f;
    public float height = 100f;

    public NinePatchVO() {}

    public NinePatchVO(String imageName, float width, float height) {
        this.imageName = imageName;
        this.itemName = imageName;
        this.width = width;
        this.height = height;
    }
}
