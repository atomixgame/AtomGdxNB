package com.atomgdx.editor.scene2d.data.vo;

/**
 * 2D Dynamic Light (Point, Cone, Directional) for box2dlights in HyperLap2D.
 */
public class LightVO extends MainItemVO {
    private static final long serialVersionUID = 1L;

    public enum LightType {
        POINT, CONE, DIRECTIONAL
    }

    public LightType type = LightType.POINT;
    public int rays = 128;
    public float distance = 300f;
    public float directionDegree = 0f;
    public float coneDegree = 45f;
    public boolean isStatic = false;
    public boolean isXRay = false;
    public boolean soft = true;
    public float softnessLength = 2.5f;

    public LightVO() {}

    public LightVO(String itemName, LightType type, float x, float y) {
        this.itemName = itemName;
        this.type = type;
        this.x = x;
        this.y = y;
    }
}
