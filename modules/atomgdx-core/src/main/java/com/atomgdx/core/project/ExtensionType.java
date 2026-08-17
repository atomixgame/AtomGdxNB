package com.atomgdx.core.project;

/**
 * LibGDX official and community extensions supported in AtomGdx Studio.
 */
public enum ExtensionType {
    BOX2D("Box2D", "2D Physics simulation engine", "com.badlogicgames.gdx:gdx-box2d"),
    BOX2DLIGHTS("Box2DLights", "2D Dynamic lighting & soft shadows", "com.badlogicgames.box2dlights:box2dlights"),
    ASHLEY("Ashley", "Entity Component System (ECS) framework", "com.badlogicgames.ashley:ashley"),
    FREETYPE("FreeType", "Vector font TTF/OTF dynamic rasterizer", "com.badlogicgames.gdx:gdx-freetype"),
    GDX_AI("gdx-ai", "Artificial intelligence (Behavior Trees, Steering, Pathfinding)", "com.badlogicgames.gdx:gdx-ai"),
    GDX_CONTROLLERS("gdx-controllers", "Gamepad and joystick support", "com.badlogicgames.gdx-controllers:gdx-controllers-core"),
    GDX_GLTF("gdx-gltf", "glTF 2.0 PBR 3D rendering pipeline", "net.mgsx.gltf:gltf"),
    VIS_UI("VisUI", "Scene2D modern flat UI widgets", "com.kotcrab.vis:vis-ui");

    private final String displayName;
    private final String description;
    private final String mavenCoordinate;

    ExtensionType(String displayName, String description, String mavenCoordinate) {
        this.displayName = displayName;
        this.description = description;
        this.mavenCoordinate = mavenCoordinate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getMavenCoordinate() {
        return mavenCoordinate;
    }
}
