package com.atomgdx.editor.scene2d.data.vo;

/**
 * Value Object representing a nested composite item (library component or grouped actors).
 */
public class CompositeItemVO extends MainItemVO {
    private static final long serialVersionUID = 1L;

    public CompositeDataObject composite = new CompositeDataObject();
    public float width = 0f;
    public float height = 0f;

    public CompositeItemVO() {}

    public CompositeItemVO(String itemName) {
        this.itemName = itemName;
    }
}
