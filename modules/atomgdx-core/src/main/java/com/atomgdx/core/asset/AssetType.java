package com.atomgdx.core.asset;

/**
 * Categorization of game assets in LibGDX projects.
 */
public enum AssetType {
    TEXTURE("Image/Texture", new String[]{".png", ".jpg", ".jpeg", ".tga", ".bmp"}),
    ATLAS("Texture Atlas", new String[]{".atlas"}),
    NINE_PATCH("9-Patch Sprite", new String[]{".9.png"}),
    PARTICLE_2D("2D Particle Effect", new String[]{".p", ".party", ".particle2d"}),
    PARTICLE_3D("3D Flame Particle", new String[]{".p3d", ".flame"}),
    SKIN("Scene2D/VisUI Skin", new String[]{".json", ".skin"}),
    SCENE_2D("2D Scene/Level (HyperLap2D)", new String[]{".scene2d", ".h2d", ".dt"}),
    MODEL_3D("3D Mesh / Model", new String[]{".g3db", ".g3dj", ".gltf", ".glb", ".obj"}),
    SHADER("GLSL Shader", new String[]{".vert", ".frag", ".glsl", ".geom"}),
    FONT("Bitmap / TrueType Font", new String[]{".fnt", ".ttf", ".otf", ".hiero"}),
    AUDIO("Audio Clip / Music", new String[]{".wav", ".mp3", ".ogg"}),
    DATA("Game Data / Config", new String[]{".xml", ".yaml", ".yml", ".csv", ".tmx"});

    private final String description;
    private final String[] extensions;

    AssetType(String description, String[] extensions) {
        this.description = description;
        this.extensions = extensions;
    }

    public String getDescription() {
        return description;
    }

    public String[] getExtensions() {
        return extensions;
    }

    public static AssetType fromFileName(String filename) {
        if (filename == null) return DATA;
        String lower = filename.toLowerCase();
        for (AssetType type : values()) {
            for (String ext : type.extensions) {
                if (lower.endsWith(ext)) {
                    return type;
                }
            }
        }
        return DATA;
    }
}
