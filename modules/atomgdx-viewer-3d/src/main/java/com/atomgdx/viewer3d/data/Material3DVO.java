package com.atomgdx.viewer3d.data;

import java.io.Serializable;

/**
 * PBR and Classic Material definition for 3D Models and Prefabs (.mat.json).
 */
public class Material3DVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String materialName = "DefaultMaterial";
    public float[] diffuseColor = new float[]{0.8f, 0.8f, 0.8f, 1.0f};
    public float[] specularColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    public float[] emissiveColor = new float[]{0.0f, 0.0f, 0.0f, 1.0f};

    public float metallic = 0.1f;
    public float roughness = 0.5f;
    public float shininess = 32.0f;
    public float opacity = 1.0f;

    public String diffuseTexturePath = "";
    public String normalTexturePath = "";
    public String metallicRoughnessTexturePath = "";
    public String emissiveTexturePath = "";

    public boolean isTwoSided = false;
    public boolean isWireframe = false;
    public boolean isTransparent = false;

    public Material3DVO() {}

    public Material3DVO(String name, float[] diffuse, float metallic, float roughness) {
        this.materialName = name;
        this.diffuseColor = diffuse;
        this.metallic = metallic;
        this.roughness = roughness;
    }

    public static Material3DVO createPreset(String name) {
        switch (name.toLowerCase()) {
            case "metallic gold":
                return new Material3DVO("Metallic Gold", new float[]{1.0f, 0.84f, 0.0f, 1.0f}, 0.9f, 0.2f);
            case "brushed steel":
                return new Material3DVO("Brushed Steel", new float[]{0.75f, 0.77f, 0.8f, 1.0f}, 0.85f, 0.35f);
            case "neon glow cyan":
                Material3DVO neon = new Material3DVO("Neon Glow Cyan", new float[]{0.1f, 0.8f, 1.0f, 1.0f}, 0.0f, 0.1f);
                neon.emissiveColor = new float[]{0.1f, 0.9f, 1.0f, 1.0f};
                return neon;
            case "scifi hull paint":
                return new Material3DVO("SciFi Hull Paint", new float[]{0.22f, 0.25f, 0.30f, 1.0f}, 0.3f, 0.6f);
            case "matte plastic":
                return new Material3DVO("Matte Plastic", new float[]{0.85f, 0.25f, 0.25f, 1.0f}, 0.0f, 0.8f);
            case "transparent glass":
                Material3DVO glass = new Material3DVO("Transparent Glass", new float[]{0.9f, 0.95f, 1.0f, 0.35f}, 0.1f, 0.05f);
                glass.isTransparent = true;
                glass.opacity = 0.35f;
                return glass;
            default:
                return new Material3DVO("Default Material", new float[]{0.7f, 0.7f, 0.7f, 1.0f}, 0.2f, 0.5f);
        }
    }
}
