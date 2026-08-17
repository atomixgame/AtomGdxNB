package com.atomgdx.editor.scene2d.data.vo;

import java.io.Serializable;

/**
 * Box2D physics simulation properties for an item in a scene.
 */
public class PhysicsBodyDataVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public int bodyType = 0; // 0: Static, 1: Kinematic, 2: Dynamic
    public float mass = 0f;
    public float density = 1f;
    public float friction = 0.2f;
    public float restitution = 0f;
    public float gravityScale = 1f;
    public boolean allowSleep = true;
    public boolean awake = true;
    public boolean bullet = false;
    public boolean sensor = false;
    public boolean fixedRotation = false;

    public short categoryBits = 0x0001;
    public short maskBits = -1;
    public short groupIndex = 0;

    public PhysicsBodyDataVO() {}
}
