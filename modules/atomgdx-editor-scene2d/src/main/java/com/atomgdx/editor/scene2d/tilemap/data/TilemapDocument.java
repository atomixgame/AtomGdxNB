package com.atomgdx.editor.scene2d.tilemap.data;

import com.atomgdx.editor.scene2d.ui.TilemapGridMode;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Root Tilemap Document for AtomGDX Level Construction Engine.
 */
public class TilemapDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name = "NewLevelMap";
    private TilemapGridMode orientation = TilemapGridMode.ORTHOGONAL;
    private int width = 50;
    private int height = 50;
    private int tileWidth = 32;
    private int tileHeight = 32;
    private int hexSideLength = 16;
    private Color backgroundColor = new Color(20, 22, 26);

    private final List<TilemapLayer> layers = new ArrayList<>();
    private final List<TileSetVO> tileSets = new ArrayList<>();
    private final PropertyMap properties = new PropertyMap();
    private int activeLayerIndex = 0;

    public TilemapDocument() {
        this("Level_01", TilemapGridMode.ORTHOGONAL, 40, 30, 32, 32);
    }

    public TilemapDocument(String name, TilemapGridMode orientation, int width, int height, int tileWidth, int tileHeight) {
        this.name = name;
        this.orientation = orientation != null ? orientation : TilemapGridMode.ORTHOGONAL;
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        // Default standard layers
        TileLayer ground = new TileLayer("Ground", width, height);
        // Fill sample ground pattern
        ground.fillRect(0, 0, width, height, 1);
        layers.add(ground);

        TileLayer obstacles = new TileLayer("Obstacles & Walls", width, height);
        obstacles.fillRect(5, 5, 10, 2, 2);
        obstacles.fillRect(5, 7, 2, 8, 2);
        obstacles.fillRect(13, 7, 2, 8, 2);
        obstacles.fillRect(5, 15, 10, 2, 2);
        layers.add(obstacles);

        ObjectGroupLayer objects = new ObjectGroupLayer("Entities & Spawns");
        objects.addObject(new MapObjectVO("PlayerSpawn", "SpawnPoint", 8 * 32, 10 * 32, 32, 32));
        objects.addObject(new MapObjectVO("LevelExitTrigger", "Trigger", 35 * 32, 25 * 32, 64, 64));
        layers.add(objects);

        // Default standard tileset
        TileSetVO defSet = new TileSetVO("Cosmic_Station_Set", 1, "tilesets/scifi_station_tiles.png", tileWidth, tileHeight);
        tileSets.add(defSet);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public TilemapGridMode getOrientation() { return orientation; }
    public void setOrientation(TilemapGridMode mode) { this.orientation = mode; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getTileWidth() { return tileWidth; }
    public int getTileHeight() { return tileHeight; }
    public int getHexSideLength() { return hexSideLength; }
    public void setHexSideLength(int len) { this.hexSideLength = len; }
    public Color getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(Color c) { this.backgroundColor = c; }

    public List<TilemapLayer> getLayers() { return layers; }
    public List<TileSetVO> getTileSets() { return tileSets; }
    public PropertyMap getProperties() { return properties; }

    public int getActiveLayerIndex() { return activeLayerIndex; }
    public void setActiveLayerIndex(int idx) {
        if (idx >= 0 && idx < layers.size()) {
            this.activeLayerIndex = idx;
        }
    }

    public TilemapLayer getActiveLayer() {
        if (activeLayerIndex >= 0 && activeLayerIndex < layers.size()) {
            return layers.get(activeLayerIndex);
        }
        return layers.isEmpty() ? null : layers.get(0);
    }

    public void addLayer(TilemapLayer layer) {
        layers.add(layer);
        activeLayerIndex = layers.size() - 1;
    }

    public void removeLayer(int index) {
        if (index >= 0 && index < layers.size()) {
            layers.remove(index);
            if (activeLayerIndex >= layers.size()) {
                activeLayerIndex = Math.max(0, layers.size() - 1);
            }
        }
    }
}
