package com.atomgdx.editor.scene2d;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Physics definition for Box2D rigid bodies in scene designer.
 */
public class Box2DPhysicsData implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum BodyType {
        STATIC,
        KINEMATIC,
        DYNAMIC
    }

    private boolean enabled = false;
    private BodyType bodyType = BodyType.STATIC;
    private float density = 1.0f;
    private float friction = 0.4f;
    private float restitution = 0.0f;
    private boolean isSensor = false;
    private final List<float[]> polygonVertices = new ArrayList<>(); // Pairs of (x, y)

    public Box2DPhysicsData() {
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public BodyType getBodyType() { return bodyType; }
    public void setBodyType(BodyType bodyType) { this.bodyType = bodyType; }

    public float getDensity() { return density; }
    public void setDensity(float density) { this.density = density; }

    public float getFriction() { return friction; }
    public void setFriction(float friction) { this.friction = friction; }

    public float getRestitution() { return restitution; }
    public void setRestitution(float restitution) { this.restitution = restitution; }

    public boolean isSensor() { return isSensor; }
    public void setSensor(boolean sensor) { isSensor = sensor; }

    public List<float[]> getPolygonVertices() { return polygonVertices; }
}
