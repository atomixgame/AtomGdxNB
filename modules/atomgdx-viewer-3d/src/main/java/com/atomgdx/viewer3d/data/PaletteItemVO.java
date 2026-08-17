package com.atomgdx.viewer3d.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Descriptor for items configured in palette_items.json.
 */
public class PaletteItemVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String id;
    public String name;
    public String category; // "2D Shapes", "2D Prefabs", "3D Primitives", "3D Prefabs", "Materials"
    public String type;     // "2D", "3D", "MATERIAL"
    public String shape;    // "BOX", "SPHERE", "CYLINDER", "CONE", "CAPSULE", "PLANE", "TORUS"
    public String icon;
    public String subtitle;
    public List<String> tags = new ArrayList<>();

    public PaletteItemVO() {}

    public PaletteItemVO(String id, String name, String category, String type, String icon, String subtitle) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.type = type;
        this.icon = icon;
        this.subtitle = subtitle;
    }

    public Object createInstance() {
        if ("MATERIAL".equalsIgnoreCase(type)) {
            return Material3DVO.createPreset(name);
        } else if ("3D".equalsIgnoreCase(type)) {
            if ("3D Prefabs".equalsIgnoreCase(category)) {
                if (name.contains("Fighter")) return Prefab3DVO.createSpacecraftFighter().rootNode;
                if (name.contains("Asteroid")) return Prefab3DVO.createAsteroidRock().rootNode;
                if (name.contains("Turret")) return Prefab3DVO.createSciFiTurret().rootNode;
                if (name.contains("Shield")) return Prefab3DVO.createEnergyShield().rootNode;
                Node3DVO custom = new Node3DVO(name.replace(" ", "_"), Node3DVO.NodeType.PREFAB);
                custom.material = Material3DVO.createPreset("scifi hull paint");
                return custom;
            } else {
                Node3DVO.MeshShape s = Node3DVO.MeshShape.BOX;
                if (shape != null) {
                    try {
                        s = Node3DVO.MeshShape.valueOf(shape);
                    } catch (Exception ignored) {}
                }
                Node3DVO node = new Node3DVO(name.replace(" ", "_"), s, 0, 0.5f, 0);
                if (s == Node3DVO.MeshShape.PLANE) {
                    node.scaleX = 10f;
                    node.scaleZ = 10f;
                    node.posY = 0f;
                }
                return node;
            }
        } else {
            // 2D Entity placeholder or descriptor
            return this;
        }
    }
}
