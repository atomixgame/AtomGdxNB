package com.atomgdx.viewer3d.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 3D SceneGraph Node representing entities, meshes, lights, cameras, and prefabs.
 */
public class Node3DVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum NodeType {
        EMPTY,
        MESH,
        LIGHT_POINT,
        LIGHT_DIRECTIONAL,
        CAMERA,
        PREFAB,
        PARTICLE_EMITTER
    }

    public enum MeshShape {
        BOX,
        SPHERE,
        CYLINDER,
        CONE,
        CAPSULE,
        PLANE,
        TORUS,
        CUSTOM_MODEL
    }

    public String nodeName = "GameObject3D";
    public NodeType nodeType = NodeType.MESH;
    public MeshShape meshShape = MeshShape.BOX;
    public String modelFilePath = "";

    // 3D Transform
    public float posX = 0f, posY = 0f, posZ = 0f;
    public float rotX = 0f, rotY = 0f, rotZ = 0f; // Euler angles in degrees
    public float scaleX = 1f, scaleY = 1f, scaleZ = 1f;

    // Mesh Statistics
    public int vertexCount = 24;
    public int triangleCount = 12;

    // Material
    public Material3DVO material = new Material3DVO();

    // Lighting (if NodeType is LIGHT)
    public float[] lightColor = new float[]{1f, 1f, 1f, 1f};
    public float lightIntensity = 1.0f;
    public float lightDistance = 25.0f;

    // Physics 3D (Bullet Physics)
    public boolean hasPhysics = false;
    public int physicsBodyType = 2; // 0=Static, 1=Kinematic, 2=Dynamic
    public float mass = 1.0f;
    public float friction = 0.5f;
    public float restitution = 0.1f;
    public String collisionShape = "BOX"; // BOX, SPHERE, CAPSULE, CONVEX_HULL

    // Hierarchy
    public boolean isVisible = true;
    public boolean isLocked = false;
    public final List<Node3DVO> children = new ArrayList<>();

    public Node3DVO() {}

    public Node3DVO(String name, NodeType type) {
        this.nodeName = name;
        this.nodeType = type;
    }

    public Node3DVO(String name, MeshShape shape, float x, float y, float z) {
        this.nodeName = name;
        this.nodeType = NodeType.MESH;
        this.meshShape = shape;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    public void addChild(Node3DVO child) {
        if (child != null && !children.contains(child)) {
            children.add(child);
        }
    }

    public boolean removeChild(Node3DVO child) {
        return children.remove(child);
    }

    @Override
    public String toString() {
        return nodeName;
    }
}
