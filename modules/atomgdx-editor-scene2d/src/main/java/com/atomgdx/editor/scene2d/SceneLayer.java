package com.atomgdx.editor.scene2d;

import java.io.Serializable;

/**
 * Represents a rendering layer with parallax factor and visibility.
 */
public class SceneLayer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name = "Default Layer";
    private boolean visible = true;
    private boolean locked = false;
    private float parallaxX = 1.0f;
    private float parallaxY = 1.0f;

    public SceneLayer(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }

    public float getParallaxX() { return parallaxX; }
    public void setParallaxX(float parallaxX) { this.parallaxX = parallaxX; }

    public float getParallaxY() { return parallaxY; }
    public void setParallaxY(float parallaxY) { this.parallaxY = parallaxY; }
}
