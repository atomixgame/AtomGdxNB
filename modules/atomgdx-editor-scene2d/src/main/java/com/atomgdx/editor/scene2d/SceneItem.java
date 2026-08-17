package com.atomgdx.editor.scene2d;

import java.io.Serializable;

/**
 * An individual item/entity placed in a 2D scene (Sprite, Particle, Light, Composite).
 */
public class SceneItem implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum ItemType {
        IMAGE,
        PARTICLE_EFFECT,
        LIGHT,
        LABEL,
        COMPOSITE
    }

    private String id;
    private String name;
    private ItemType itemType = ItemType.IMAGE;
    private String layerName = "Default Layer";
    private String resourcePath = ""; // e.g. texture region or particle file

    // Transform
    private float x = 0f;
    private float y = 0f;
    private float width = 64f;
    private float height = 64f;
    private float scaleX = 1f;
    private float scaleY = 1f;
    private float rotation = 0f;
    private float originX = 32f;
    private float originY = 32f;
    private float alpha = 1.0f;
    private String tintColorHex = "ffffff";

    // Optional Physics & Light
    private Box2DPhysicsData physicsData;
    private LightData lightData;

    public SceneItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public SceneItem(String id, String name, float x, float y, float width, float height, String layerName) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.layerName = layerName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ItemType getItemType() { return itemType; }
    public void setItemType(ItemType itemType) { this.itemType = itemType; }

    public String getLayerName() { return layerName; }
    public void setLayerName(String layerName) { this.layerName = layerName; }

    public String getResourcePath() { return resourcePath; }
    public void setResourcePath(String resourcePath) { this.resourcePath = resourcePath; }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }

    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public float getScaleX() { return scaleX; }
    public void setScaleX(float scaleX) { this.scaleX = scaleX; }

    public float getScaleY() { return scaleY; }
    public void setScaleY(float scaleY) { this.scaleY = scaleY; }

    public float getRotation() { return rotation; }
    public void setRotation(float rotation) { this.rotation = rotation; }

    public float getOriginX() { return originX; }
    public void setOriginX(float originX) { this.originX = originX; }

    public float getOriginY() { return originY; }
    public void setOriginY(float originY) { this.originY = originY; }

    public float getAlpha() { return alpha; }
    public void setAlpha(float alpha) { this.alpha = alpha; }

    public String getTintColorHex() { return tintColorHex; }
    public void setTintColorHex(String tintColorHex) { this.tintColorHex = tintColorHex; }

    public Box2DPhysicsData getPhysicsData() { return physicsData; }
    public void setPhysicsData(Box2DPhysicsData physicsData) { this.physicsData = physicsData; }

    public LightData getLightData() { return lightData; }
    public void setLightData(LightData lightData) { this.lightData = lightData; }
}
