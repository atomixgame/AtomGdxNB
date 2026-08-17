package com.atomgdx.editor.scene2d.data.vo;

import java.io.Serializable;

/**
 * Color data structure supporting RGBA float components.
 */
public class ColorDataVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public float r = 1f;
    public float g = 1f;
    public float b = 1f;
    public float a = 1f;

    public ColorDataVO() {}

    public ColorDataVO(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}
