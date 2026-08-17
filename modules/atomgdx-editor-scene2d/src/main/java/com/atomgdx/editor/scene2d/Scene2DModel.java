package com.atomgdx.editor.scene2d;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model representing a 2D Scene with multi-layer hierarchy, entities, Box2D physics, and lights.
 */
public class Scene2DModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sceneName = "New Scene";
    private int sceneWidth = 1920;
    private int sceneHeight = 1080;
    private String ambientColorHex = "1a1a24";
    private boolean physicsEnabled = true;
    private float gravityX = 0f;
    private float gravityY = -9.8f;

    private final List<SceneLayer> layers = new ArrayList<>();
    private final List<SceneItem> items = new ArrayList<>();

    public Scene2DModel() {
        layers.add(new SceneLayer("Background"));
        layers.add(new SceneLayer("Main"));
        layers.add(new SceneLayer("Foreground"));
        layers.add(new SceneLayer("UI"));
    }

    public Scene2DModel(String sceneName) {
        this();
        this.sceneName = sceneName;
    }

    public String getSceneName() { return sceneName; }
    public void setSceneName(String sceneName) { this.sceneName = sceneName; }

    public int getSceneWidth() { return sceneWidth; }
    public void setSceneWidth(int sceneWidth) { this.sceneWidth = sceneWidth; }

    public int getSceneHeight() { return sceneHeight; }
    public void setSceneHeight(int sceneHeight) { this.sceneHeight = sceneHeight; }

    public String getAmbientColorHex() { return ambientColorHex; }
    public void setAmbientColorHex(String ambientColorHex) { this.ambientColorHex = ambientColorHex; }

    public boolean isPhysicsEnabled() { return physicsEnabled; }
    public void setPhysicsEnabled(boolean physicsEnabled) { this.physicsEnabled = physicsEnabled; }

    public float getGravityX() { return gravityX; }
    public void setGravityX(float gravityX) { this.gravityX = gravityX; }

    public float getGravityY() { return gravityY; }
    public void setGravityY(float gravityY) { this.gravityY = gravityY; }

    public List<SceneLayer> getLayers() { return Collections.unmodifiableList(layers); }
    public void addLayer(SceneLayer layer) { layers.add(layer); }
    public void removeLayer(SceneLayer layer) { if (layers.size() > 1) layers.remove(layer); }

    public List<SceneItem> getItems() { return Collections.unmodifiableList(items); }
    public void addItem(SceneItem item) { items.add(item); }
    public void removeItem(SceneItem item) { items.remove(item); }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{\n");
        sb.append("  \"sceneName\": \"").append(sceneName).append("\",\n");
        sb.append("  \"sceneWidth\": ").append(sceneWidth).append(",\n");
        sb.append("  \"sceneHeight\": ").append(sceneHeight).append(",\n");
        sb.append("  \"ambientColorHex\": \"").append(ambientColorHex).append("\",\n");
        sb.append("  \"physicsEnabled\": ").append(physicsEnabled).append(",\n");
        sb.append("  \"layers\": [\n");
        for (int i = 0; i < layers.size(); i++) {
            SceneLayer l = layers.get(i);
            sb.append("    { \"name\": \"").append(l.getName()).append("\", \"parallaxX\": ").append(l.getParallaxX()).append(", \"parallaxY\": ").append(l.getParallaxY()).append(" }");
            if (i + 1 < layers.size()) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ],\n");
        sb.append("  \"items\": [\n");
        for (int i = 0; i < items.size(); i++) {
            SceneItem item = items.get(i);
            sb.append("    { \"id\": \"").append(item.getId()).append("\", \"name\": \"").append(item.getName()).append("\", \"x\": ").append(item.getX()).append(", \"y\": ").append(item.getY());
            if (item.getPhysicsData() != null) {
                sb.append(", \"bodyType\": \"").append(item.getPhysicsData().getBodyType()).append("\"");
            }
            if (item.getLightData() != null) {
                sb.append(", \"lightColor\": \"").append(item.getLightData().getColorHex()).append("\"");
            }
            sb.append(" }");
            if (i + 1 < items.size()) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }
}
