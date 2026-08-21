package com.atomgdx.editor.scene2d.tilemap.data;

import java.awt.Color;
import java.io.Serializable;

/**
 * Base abstract class for tilemap layers (TileLayer, ObjectGroupLayer, ImageLayer).
 */
public abstract class TilemapLayer implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum LayerType {
        TILE_LAYER,
        OBJECT_GROUP,
        IMAGE_LAYER,
        GROUP_LAYER
    }

    private String name;
    private LayerType type;
    private float opacity = 1.0f;
    private boolean visible = true;
    private boolean locked = false;
    private Color tintColor = Color.WHITE;
    private float parallaxX = 1.0f;
    private float parallaxY = 1.0f;
    private float offsetX = 0.0f;
    private float offsetY = 0.0f;
    private final PropertyMap properties = new PropertyMap();

    public TilemapLayer(String name, LayerType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LayerType getType() { return type; }
    public float getOpacity() { return opacity; }
    public void setOpacity(float opacity) { this.opacity = Math.max(0f, Math.min(1f, opacity)); }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
    public Color getTintColor() { return tintColor; }
    public void setTintColor(Color tintColor) { this.tintColor = tintColor != null ? tintColor : Color.WHITE; }
    public float getParallaxX() { return parallaxX; }
    public void setParallaxX(float parallaxX) { this.parallaxX = parallaxX; }
    public float getParallaxY() { return parallaxY; }
    public void setParallaxY(float parallaxY) { this.parallaxY = parallaxY; }
    public float getOffsetX() { return offsetX; }
    public void setOffsetX(float offsetX) { this.offsetX = offsetX; }
    public float getOffsetY() { return offsetY; }
    public void setOffsetY(float offsetY) { this.offsetY = offsetY; }
    public PropertyMap getProperties() { return properties; }
}
