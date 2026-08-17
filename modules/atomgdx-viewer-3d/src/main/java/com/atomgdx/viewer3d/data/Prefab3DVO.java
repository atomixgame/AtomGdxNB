package com.atomgdx.viewer3d.data;

import java.io.Serializable;

/**
 * 3D Prefab definition (.prefab.json) representing reusable game entities.
 */
public class Prefab3DVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String prefabName = "New Prefab";
    public String category = "General";
    public String iconName = "brick.png";
    public Node3DVO rootNode;

    public Prefab3DVO() {}

    public Prefab3DVO(String name, String category, String icon, Node3DVO root) {
        this.prefabName = name;
        this.category = category;
        this.iconName = icon;
        this.rootNode = root;
    }

    public static Prefab3DVO createSpacecraftFighter() {
        Node3DVO ship = new Node3DVO("Spacecraft_Fighter", Node3DVO.MeshShape.CUSTOM_MODEL, 0, 1.5f, 0);
        ship.vertexCount = 1420;
        ship.triangleCount = 890;
        ship.material = Material3DVO.createPreset("scifi hull paint");
        ship.hasPhysics = true;
        ship.mass = 500f;

        Node3DVO thrusterLight = new Node3DVO("Thruster_Glow", Node3DVO.NodeType.LIGHT_POINT);
        thrusterLight.posZ = -2.5f;
        thrusterLight.lightColor = new float[]{0.1f, 0.8f, 1f, 1f};
        thrusterLight.lightIntensity = 3.5f;
        ship.addChild(thrusterLight);

        return new Prefab3DVO("Spacecraft Fighter", "Vehicles", "car.png", ship);
    }

    public static Prefab3DVO createAsteroidRock() {
        Node3DVO rock = new Node3DVO("Asteroid_Large", Node3DVO.MeshShape.SPHERE, 0, 0, 0);
        rock.scaleX = 2.5f;
        rock.scaleY = 2.0f;
        rock.scaleZ = 2.8f;
        rock.vertexCount = 640;
        rock.triangleCount = 380;
        rock.material = new Material3DVO("Asteroid Ore", new float[]{0.45f, 0.40f, 0.35f, 1f}, 0.6f, 0.8f);
        rock.hasPhysics = true;
        rock.physicsBodyType = 0; // Static
        return new Prefab3DVO("Asteroid Rock", "Environment", "world.png", rock);
    }

    public static Prefab3DVO createSciFiTurret() {
        Node3DVO turret = new Node3DVO("Defense_Turret_Base", Node3DVO.MeshShape.CYLINDER, 0, 0.5f, 0);
        turret.material = Material3DVO.createPreset("brushed steel");

        Node3DVO cannon = new Node3DVO("Turret_Cannon", Node3DVO.MeshShape.BOX, 0, 1.2f, 0.8f);
        cannon.scaleX = 0.4f;
        cannon.scaleY = 0.4f;
        cannon.scaleZ = 1.8f;
        cannon.material = Material3DVO.createPreset("metallic gold");
        turret.addChild(cannon);

        return new Prefab3DVO("SciFi Turret", "Defense", "shield.png", turret);
    }

    public static Prefab3DVO createEnergyShield() {
        Node3DVO shield = new Node3DVO("Energy_Shield_Bubble", Node3DVO.MeshShape.SPHERE, 0, 1f, 0);
        shield.scaleX = 3.0f;
        shield.scaleY = 3.0f;
        shield.scaleZ = 3.0f;
        shield.material = Material3DVO.createPreset("neon glow cyan");
        return new Prefab3DVO("Energy Shield", "FX", "lightning.png", shield);
    }
}
