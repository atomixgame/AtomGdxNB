package com.atomgdx.viewer3d.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 3D Scene descriptor containing the SceneGraph root and environment lighting.
 */
public class Scene3DVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String sceneName = "New Scene 3D";
    public final Node3DVO rootNode;
    public float[] ambientLightColor = new float[]{0.35f, 0.40f, 0.48f, 1.0f};
    public float[] directionalLightColor = new float[]{0.95f, 0.95f, 1.0f, 1.0f};
    public float[] directionalLightDir = new float[]{-1f, -0.8f, -0.3f};

    public Scene3DVO() {
        this("MainScene3D");
    }

    public Scene3DVO(String name) {
        this.sceneName = name;
        this.rootNode = new Node3DVO("Scene Root", Node3DVO.NodeType.EMPTY);
        initDefaultGraph();
    }

    private void initDefaultGraph() {
        // Environment & Lights
        Node3DVO envGroup = new Node3DVO("Environment & Lights", Node3DVO.NodeType.EMPTY);
        Node3DVO sun = new Node3DVO("Sun_DirectionalLight", Node3DVO.NodeType.LIGHT_DIRECTIONAL);
        envGroup.addChild(sun);
        rootNode.addChild(envGroup);

        // Main Camera
        Node3DVO camNode = new Node3DVO("Main_PerspectiveCamera", Node3DVO.NodeType.CAMERA);
        camNode.posX = 5f;
        camNode.posY = 6f;
        camNode.posZ = 8f;
        camNode.rotX = -30f;
        camNode.rotY = 35f;
        rootNode.addChild(camNode);

        // Default Spacecraft Model
        Prefab3DVO shipPrefab = Prefab3DVO.createSpacecraftFighter();
        rootNode.addChild(shipPrefab.rootNode);

        // Orbiting Asteroid
        Prefab3DVO asteroidPrefab = Prefab3DVO.createAsteroidRock();
        asteroidPrefab.rootNode.posX = -5.0f;
        asteroidPrefab.rootNode.posZ = -3.0f;
        rootNode.addChild(asteroidPrefab.rootNode);
    }
}
